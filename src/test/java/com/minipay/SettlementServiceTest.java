package com.minipay;

import com.minipay.api.merchant.domain.Merchant;
import com.minipay.api.merchant.service.MerchantService;
import com.minipay.api.payment.domains.Payment;
import com.minipay.api.payment.enums.PaymentStatus;
import com.minipay.api.payment.service.PaymentService;
import com.minipay.api.settlement.domains.SettlementBatch;
import com.minipay.api.settlement.domains.SettlementItem;
import com.minipay.api.settlement.dto.response.SettlementBatchResponse;
import com.minipay.api.settlement.repository.SettlementBatchRepository;
import com.minipay.api.settlement.repository.SettlementItemRepository;
import com.minipay.api.settlement.service.impl.SettlementServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class SettlementServiceTest {

    @InjectMocks
    private SettlementServiceImpl settlementService;

    @Mock
    private PaymentService paymentService;

    @Mock
    private MerchantService merchantService;

    @Mock
    private SettlementBatchRepository batchRepository;

    @Mock
    private SettlementItemRepository itemRepository;

    private Payment payment1;
    private Payment payment2;
    private Merchant merchant;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        merchant = new Merchant();
        merchant.setMerchantId("M123");

        payment1 = new Payment();
        payment1.setAmount(new BigDecimal("1000"));
        payment1.setMsc(new BigDecimal("20"));
        payment1.setVatAmount(new BigDecimal("1.5"));
        payment1.setProcessorFee(new BigDecimal("10"));
        payment1.setProcessorVat(new BigDecimal("0.75"));
        payment1.setMerchant(merchant);
        payment1.setPaymentStatus(PaymentStatus.SUCCESS);

        payment2 = new Payment();
        payment2.setAmount(new BigDecimal("500"));
        payment2.setMsc(new BigDecimal("10"));
        payment2.setVatAmount(new BigDecimal("0.75"));
        payment2.setProcessorFee(new BigDecimal("5"));
        payment2.setProcessorVat(new BigDecimal("0.375"));
        payment2.setMerchant(merchant);
        payment2.setPaymentStatus(PaymentStatus.SUCCESS);
    }

    @Test
    void testGenerateSettlementBatch() {
        LocalDate from = LocalDate.of(2025, 10, 1);
        LocalDate to = LocalDate.of(2025, 10, 1);

        when(paymentService.getUnsettledPayments(eq(PaymentStatus.SUCCESS), any(), any()))
                .thenReturn(List.of(payment1, payment2));
        when(merchantService.getMerchantById("M123")).thenReturn(merchant);
        when(batchRepository.save(any(SettlementBatch.class))).thenAnswer(inv -> inv.getArgument(0));
        when(itemRepository.save(any(SettlementItem.class))).thenAnswer(inv -> inv.getArgument(0));
        when(paymentService.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

        SettlementBatchResponse response = settlementService.generate(from, to);

        assertNotNull(response);
        assertEquals(new BigDecimal("1500"), response.getAmount());
        assertEquals(new BigDecimal("30"), response.getMsc());
        assertEquals(new BigDecimal("2.25"), response.getVatAmount());
        assertEquals(new BigDecimal("15"), response.getProcessorFee());
        assertEquals(new BigDecimal("1.125"), response.getProcessorVat());
        assertEquals(new BigDecimal("15"), response.getIncome());
        assertEquals(new BigDecimal("1.125"), response.getPayableVat());
        assertEquals(new BigDecimal("1451.625"), response.getAmountPayable());

        verify(batchRepository, times(1)).save(any(SettlementBatch.class));
        verify(itemRepository, times(2)).save(any(SettlementItem.class));
        verify(paymentService, times(2)).save(any(Payment.class));
    }
}
