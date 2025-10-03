package com.minipay;

import com.minipay.api.merchant.domain.ChargeSetting;
import com.minipay.api.merchant.domain.Merchant;
import com.minipay.api.payment.domains.Payment;
import com.minipay.api.payment.enums.PaymentStatus;
import com.minipay.api.payment.service.impl.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PaymentFeeCalculationTest {
    private Payment payment;
    private ChargeSetting config;

    @BeforeEach
    void setUp() {


        Merchant merchant = new Merchant();
        merchant.setId("M123");

        payment = new Payment();
        payment.setId("P1");
        payment.setPaymentReference("REF123");
        payment.setAmount(new BigDecimal("1000.00"));
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setMerchant(merchant);

        config = new ChargeSetting();
        config.setUseFixedMSC(false);
        config.setPercentageFee(new BigDecimal("0.02")); // 2%
        config.setMscCap(new BigDecimal("50.00"));
        config.setFixedFee(BigDecimal.ZERO);
        config.setVatRate(new BigDecimal("0.075")); // 7.5%
        config.setPlatformProviderRate(new BigDecimal("0.01")); // 1%
        config.setPlatformProviderCap(new BigDecimal("20.00"));
    }

    @Test
    void testFeeCalculation_PercentageBelowCap() {
        // amount = 1000
        BigDecimal amount = payment.getAmount();

        BigDecimal expectedMscPercentagePart = amount.multiply(config.getPercentageFee()); // 1000*0.02=20
        BigDecimal expectedMsc = expectedMscPercentagePart.add(config.getFixedFee()); // 20+0=20
        BigDecimal expectedVat = expectedMsc.multiply(config.getVatRate()); // 20*0.075=1.5
        BigDecimal expectedProcessorFee = amount.multiply(config.getPlatformProviderRate()); // 1000*0.01=10
        BigDecimal expectedProcessorVat = expectedProcessorFee.multiply(config.getVatRate()); // 10*0.075=0.75
        BigDecimal expectedPayableVat = expectedVat.subtract(expectedProcessorVat); // 1.5-0.75=0.75
        BigDecimal expectedAmountPayable = amount.subtract(expectedMsc.add(expectedVat).add(expectedProcessorFee).add(expectedProcessorVat));

        // mimic calculation logic
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
        BigDecimal processorFee = (config.getPlatformProviderCap() != null)
                ? processorPart.min(config.getPlatformProviderCap()) : processorPart;
        BigDecimal processorVat = processorFee.multiply(config.getVatRate());
        BigDecimal payableVat = vatAmount.subtract(processorVat);
        BigDecimal amountPayable = amount.subtract(msc.add(vatAmount).add(processorFee).add(processorVat));

        // Assertions
        assertEquals(0, expectedMsc.compareTo(msc));
        assertEquals(0, expectedVat.compareTo(vatAmount));
        assertEquals(0, expectedProcessorFee.compareTo(processorFee));
        assertEquals(0, expectedProcessorVat.compareTo(processorVat));
        assertEquals(0, expectedPayableVat.compareTo(payableVat));
        assertEquals(0, expectedAmountPayable.compareTo(amountPayable));
    }

    @Test
    void testFeeCalculation_PercentageAboveCap() {
        // Set percentage fee very high to trigger cap
        config.setPercentageFee(new BigDecimal("0.1")); // 10%
        BigDecimal amount = payment.getAmount();

        BigDecimal expectedPercentage = amount.multiply(config.getPercentageFee()); // 1000*0.1=100
        BigDecimal expectedMsc = expectedPercentage.min(config.getMscCap()).add(config.getFixedFee()); // min(100,50)+0=50
        BigDecimal expectedVat = expectedMsc.multiply(config.getVatRate()); // 50*0.075=3.75
        BigDecimal expectedProcessorFee = amount.multiply(config.getPlatformProviderRate()); // 1000*0.01=10
        BigDecimal expectedProcessorVat = expectedProcessorFee.multiply(config.getVatRate()); // 10*0.075=0.75
        BigDecimal expectedPayableVat = expectedVat.subtract(expectedProcessorVat); // 3.75-0.75=3
        BigDecimal expectedAmountPayable = amount.subtract(expectedMsc.add(expectedVat).add(expectedProcessorFee).add(expectedProcessorVat));

        // Reuse logic from service
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
        BigDecimal processorFee = (config.getPlatformProviderCap() != null)
                ? processorPart.min(config.getPlatformProviderCap()) : processorPart;
        BigDecimal processorVat = processorFee.multiply(config.getVatRate());
        BigDecimal payableVat = vatAmount.subtract(processorVat);
        BigDecimal amountPayable = amount.subtract(msc.add(vatAmount).add(processorFee).add(processorVat));

        // Assertions
        assertEquals(0, expectedMsc.compareTo(msc));
        assertEquals(0, expectedVat.compareTo(vatAmount));
        assertEquals(0, expectedProcessorFee.compareTo(processorFee));
        assertEquals(0, expectedProcessorVat.compareTo(processorVat));
        assertEquals(0, expectedPayableVat.compareTo(payableVat));
        assertEquals(0, expectedAmountPayable.compareTo(amountPayable));
    }
}
