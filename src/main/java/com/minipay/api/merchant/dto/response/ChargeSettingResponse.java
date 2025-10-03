package com.minipay.api.merchant.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ChargeSettingResponse {
    private BigDecimal percentageFee;
    private BigDecimal fixedFee;
    private boolean useFixedMSC;
    private BigDecimal mscCap;
    private BigDecimal vatRate;
    private BigDecimal platformProviderRate;
    private BigDecimal platformProviderCap;
}
