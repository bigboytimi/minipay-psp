package com.minipay.api.settlement.service.impl;

import com.minipay.api.merchant.domain.Merchant;
import com.minipay.api.merchant.service.MerchantService;
import com.minipay.api.payment.domains.Payment;
import com.minipay.api.payment.enums.PaymentStatus;
import com.minipay.api.payment.service.PaymentService;
import com.minipay.api.settlement.domains.SettlementBatch;
import com.minipay.api.settlement.domains.SettlementItem;
import com.minipay.api.settlement.dto.response.SettlementBatchResponse;
import com.minipay.api.settlement.dto.response.SettlementDetailsResponse;
import com.minipay.api.settlement.dto.response.SettlementItemResponse;
import com.minipay.api.settlement.enums.SettlementStatus;
import com.minipay.api.settlement.repository.SettlementBatchRepository;
import com.minipay.api.settlement.repository.SettlementItemRepository;
import com.minipay.api.settlement.service.SettlementService;
import com.minipay.common.ApiResponse;
import com.minipay.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SettlementServiceImpl implements SettlementService {

    private final PaymentService paymentService;
    private final MerchantService merchantService;
    private final SettlementBatchRepository batchRepository;
    private final SettlementItemRepository itemRepository;

    @Override
    @Transactional
    public SettlementBatchResponse generate(LocalDate from, LocalDate to) {
        LocalDateTime start = from.atStartOfDay();
        LocalDateTime end = to.atTime(23, 59, 59);

        List<Payment> paymentTxns = paymentService.getUnsettledPayments(PaymentStatus.SUCCESS, start, end);

        if (paymentTxns.isEmpty()) {
            throw new ApiException("No unsettled transactions found for period.");
        }

        Map<String, List<Payment>> grouped = paymentTxns.stream()
                .collect(Collectors.groupingBy(t -> t.getMerchant().getMerchantId()));

        SettlementBatch batchResult = null;
        for (Map.Entry<String, List<Payment>> entry : grouped.entrySet()) {
            List<Payment> merchantTxns = entry.getValue();

            Merchant merchant = merchantService.getMerchantById(entry.getKey());

            BigDecimal transactionAmount = merchantTxns.stream().map(Payment::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal msc = merchantTxns.stream().map(Payment::getMsc).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal vatAmount = merchantTxns.stream().map(Payment::getVatAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal processorFee = merchantTxns.stream().map(Payment::getProcessorFee).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal processorVat = merchantTxns.stream().map(Payment::getProcessorVat).reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal income = msc.subtract(processorFee);
            BigDecimal payableVat = vatAmount.subtract(processorVat);
            BigDecimal amountPayable = transactionAmount
                    .subtract(msc.add(vatAmount).add(processorFee).add(processorVat));

            SettlementBatch batch = new SettlementBatch();
            batch.setSettlementReference(generateSettlementRef());
            batch.setMerchant(merchant);
            batch.setCount((long) merchantTxns.size());
            batch.setAmount(transactionAmount);
            batch.setMsc(msc);
            batch.setVatAmount(vatAmount);
            batch.setProcessorFee(processorFee);
            batch.setProcessorVat(processorVat);
            batch.setIncome(income);
            batch.setPayableVat(payableVat);
            batch.setAmountPayable(amountPayable);
            batch.setPeriodStart(from);
            batch.setPeriodEnd(to);

            batchResult = batchRepository.save(batch);

            for (Payment payment : merchantTxns) {
                SettlementItem item = setSettlementItem(payment, batchResult);

                itemRepository.save(item);

                payment.setSettled(true);
                payment.setSettledAt(LocalDateTime.now());
                paymentService.save(payment);
            }
        }

        return toResponse(batchResult);
    }


    @Override
    public List<SettlementBatchResponse> batch(String batchReference, SettlementStatus status, String merchantId) {
        Specification<SettlementBatch> spec = (root, query, cb) -> cb.conjunction();

        if (batchReference != null && !batchReference.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("settlementRef"), batchReference));
        }

        if (status != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("status"), status.name()));
        }

        if (merchantId != null && !merchantId.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("merchantId"), merchantId));
        }

        List<SettlementBatch> batches = batchRepository.findAll(spec);

        return batches.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public SettlementDetailsResponse single(String settlementRef) {
        SettlementDetailsResponse response = new SettlementDetailsResponse();
        SettlementBatch settlementBatch = batchRepository.findBySettlementReference(settlementRef)
                .orElseThrow(()-> new ApiException("Record not found for reference "+settlementRef));

        List<SettlementItem> items = itemRepository.findByBatch(settlementBatch);

        List<SettlementItemResponse> itemResponses = items.stream().map(this::toResponse).toList();

        response.setItems(itemResponses);
        response.setBatch(toResponse(settlementBatch));
        return response;
    }

    @Override
    public List<SettlementBatch> getSettlementBatch(String batchReference, String merchantId, SettlementStatus status) {
        Specification<SettlementBatch> spec = (root, query, cb) -> cb.conjunction();

        if (batchReference != null && !batchReference.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("settlementRef"), batchReference));
        }

        if (status != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("status"), status.name()));
        }

        if (merchantId != null && !merchantId.isEmpty()) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("merchantId"), merchantId));
        }

        return batchRepository.findAll(spec);
    }

    private SettlementBatchResponse toResponse(SettlementBatch batchResult) {
        SettlementBatchResponse response = new SettlementBatchResponse();
        BeanUtils.copyProperties(batchResult, response);
        return response;
    }

    private SettlementItemResponse toResponse(SettlementItem settlementItem) {
        SettlementItemResponse response = new SettlementItemResponse();
        BeanUtils.copyProperties(settlementItem, response);
        return response;
    }

    private SettlementItem setSettlementItem(Payment payment, SettlementBatch batchResult) {
        SettlementItem item = new SettlementItem();
        item.setBatch(batchResult);
        item.setPayment(payment);
        item.setAmount(payment.getAmount());
        item.setMsc(payment.getMsc());
        item.setVatAmount(payment.getVatAmount());
        item.setProcessorFee(payment.getProcessorFee());
        item.setProcessorVat(payment.getProcessorVat());
        item.setAmountPayable(payment.getAmount()
                .subtract(payment.getMsc().add(payment.getVatAmount()).add(payment.getProcessorFee()).add(payment.getProcessorVat())));
        return item;
    }

    private String generateSettlementRef() {
        return "SETT" + LocalDate.now() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

}
