package com.minipay.api.payment.service.impl;

import com.minipay.api.merchant.domain.ChargeSetting;
import com.minipay.api.merchant.domain.Merchant;
import com.minipay.api.merchant.service.MerchantService;
import com.minipay.api.merchant.service.impl.ChargeService;
import com.minipay.api.payment.domains.Payment;
import com.minipay.api.payment.dto.request.ProcessPaymentRequest;
import com.minipay.api.payment.dto.request.InitiatePaymentRequest;
import com.minipay.api.payment.dto.response.PaymentResponse;
import com.minipay.api.payment.enums.Currency;
import com.minipay.api.payment.enums.PaymentChannel;
import com.minipay.api.payment.enums.PaymentStatus;
import com.minipay.api.payment.repository.PaymentRepository;
import com.minipay.api.payment.service.CallbackService;
import com.minipay.api.payment.service.PaymentService;
import com.minipay.api.settlement.domains.SettlementBatch;
import com.minipay.common.PageResponse;
import com.minipay.exception.ApiException;
import com.minipay.utils.MoneyUtils;
import com.minipay.utils.RandomGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author timilehinolowookere
 * @date 9/19/25
 */
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    private final ChargeService chargeService;

    private final MerchantService merchantService;

    private final CallbackService callbackService;

    private static final int TWELVE_DIGITS = 12;


    @Override
    public PaymentResponse payment(InitiatePaymentRequest request) {

        Merchant merchant = merchantService.getMerchantById(request.getCustomerId());

        ChargeSetting config = chargeService.getByMerchant(merchant);

        if (config == null) {
            throw new ApiException("This customer ID has no existing charge configuration");
        }

        BigDecimal amount = MoneyUtils.scale(request.getAmount());

        BigDecimal msc;
        if (config.getUseFixedMSC()) {
            msc = config.getFixedFee();
        } else {
            BigDecimal percentagePart = amount.multiply(config.getPercentageFee());
            BigDecimal cappedPart = (config.getMscCap() != null) ? percentagePart.min(config.getMscCap()) : percentagePart;
            msc = cappedPart.add(config.getFixedFee() != null ? config.getFixedFee() : BigDecimal.ZERO);
        }

        BigDecimal vatAmount = msc.multiply(config.getVatRate());

        BigDecimal processorPart = amount.multiply(config.getPlatformProviderRate());
        BigDecimal processorFee = (config.getPlatformProviderCap() != null) ? processorPart.min(config.getPlatformProviderCap()) : processorPart;

        BigDecimal processorVat = processorFee.multiply(config.getVatRate());

        BigDecimal payableVat = vatAmount.subtract(processorVat);

        BigDecimal amountPayable = amount.subtract(msc.add(vatAmount).add(processorFee).add(processorVat));

        String paymentRef = RandomGenerator.generateRandomNumber(TWELVE_DIGITS);

        Payment payment = new Payment();

        payment.setPaymentReference(paymentRef);
        payment.setOrderId(request.getOrderId());
        payment.setCustomerId(request.getCustomerId());
        payment.setAmount(amount);
        payment.setMerchant(merchant);
        payment.setCurrency(Currency.valueOf(request.getCurrency()));
        payment.setPaymentChannel(PaymentChannel.valueOf(request.getChannel()));
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setCustomerId(request.getCustomerId());
        payment.setMsc(MoneyUtils.scale(msc));
        payment.setVatAmount(MoneyUtils.scale(vatAmount));
        payment.setProcessorFee(MoneyUtils.scale(processorFee));
        payment.setProcessorVat(MoneyUtils.scale(processorVat));
        payment.setPayableVat(MoneyUtils.scale(payableVat));
        payment.setAmountPayable(MoneyUtils.scale(amountPayable));
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());

        Payment savedPaymentDetails = paymentRepository.save(payment);

        return toResponse(savedPaymentDetails);
    }

    @Override
    public void save(Payment payment) {
        try {
            paymentRepository.save(payment);
        } catch (Exception e) {
            throw new ApiException(e.getMessage());
        }
    }


    @Override
    public PaymentResponse fetch(String paymentRef) {
        Payment payment = paymentRepository.findByPaymentReference(paymentRef)
                .orElseThrow(() -> new ApiException("Payment not found"));

        return toResponse(payment);
    }

    @Override
    public PaymentResponse processPayment(ProcessPaymentRequest request) {
        return processPayment(request.getPaymentReference(), request.isStatus());
    }

    @Override
    public List<Payment> getUnsettledPayments(PaymentStatus status, LocalDateTime from, LocalDateTime to) {
        return paymentRepository.findByPaymentStatusAndSettledFalseAndCreatedAtBetween(status, from, to);
    }

    @Override
    public List<Payment> getPayments(LocalDateTime startDate, LocalDateTime endDate, PaymentChannel channel, String merchantId, PaymentStatus status) {

        Merchant merchant = null;
        if (merchantId != null) {
            merchant = merchantService.getMerchantById(merchantId);
            if (merchant == null) {
                throw new ApiException("Merchant not found for ID: " + merchantId);
            }
        }
        return paymentRepository.filterPayments(startDate, endDate, merchant, channel, status);
    }

    @Override
    public List<PaymentResponse> filter(LocalDateTime startDate, LocalDateTime endDate, String merchantId, String channel, String status) {
        Merchant merchant = null;
        if (merchantId != null) {
            merchant = merchantService.getMerchantById(merchantId);
            if (merchant == null) {
                throw new ApiException("Merchant not found for ID: " + merchantId);
            }
        }

        PaymentStatus paymentStatus = null;
        if (status != null) {
            try {
                paymentStatus = PaymentStatus.fromString(status);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid payment status: " + status);
            }
        }

        PaymentChannel paymentChannel = null;
        if (channel != null) {
            try {
                paymentChannel = PaymentChannel.valueOf(channel.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid payment channel: " + channel);
            }
        }

        List<Payment> payments = paymentRepository.filterPayments(startDate, endDate, merchant, paymentChannel, paymentStatus);

        if (payments.isEmpty()) {
            throw new ApiException("No payment records found");
        }

        return payments.stream().map(this::toResponse).toList();
    }

    public List<Payment> fetchPendingPayments() {
        return paymentRepository.findByPaymentStatus(PaymentStatus.PENDING);
    }

    private PaymentResponse toResponse(Payment payment) {
        PaymentResponse res = new PaymentResponse();
        res.setPaymentReference(payment.getPaymentReference());
        res.setOrderId(payment.getOrderId());
        res.setCustomerId(payment.getCustomerId());
        res.setChannel(payment.getPaymentChannel().name());
        res.setStatus(payment.getPaymentStatus().name());
        res.setMsc(payment.getMsc());
        res.setVatAmount(payment.getVatAmount());
        res.setProcessorFee(payment.getProcessorFee());
        res.setProcessorVat(payment.getProcessorVat());
        res.setPayableVat(payment.getPayableVat());
        res.setAmountPayable(payment.getAmountPayable());
        return res;
    }

    public PaymentResponse processPayment(String paymentRef, boolean success) {
        Payment payment = paymentRepository.findByPaymentReference(paymentRef)
                .orElseThrow(() -> new ApiException("Payment not found"));


        if (payment.getPaymentStatus() != PaymentStatus.PENDING) {
            throw new IllegalStateException("Payment is already processed");
        }

        payment.setPaymentStatus(success ? PaymentStatus.SUCCESS : PaymentStatus.FAILED);
        payment.setUpdatedAt(LocalDateTime.now());

        Payment updatedPayment = paymentRepository.save(payment);

        Merchant merchant = updatedPayment.getMerchant();

        String callbackUrl = merchant.getCallBackUrl();

        String webhookSecret = merchant.getWebhookSecret();

        if (StringUtils.hasText(callbackUrl) && StringUtils.hasText(webhookSecret)) {
            callbackService.sendWebhook(updatedPayment, callbackUrl, webhookSecret);
        }
        return toResponse(updatedPayment);
    }

}
