package com.minipay.api.merchant.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ChargeSettingRequest {
    @NotNull(groups = OnCreate.class)
    private BigDecimal percentageFee;

    @NotNull(groups = OnCreate.class)
    private BigDecimal fixedFee;

    @NotNull(groups = OnCreate.class)
    private Boolean useFixedMSC;

    @NotNull(groups = OnCreate.class)
    private BigDecimal mscCap;

    @NotNull(groups = OnCreate.class)
    private BigDecimal vatRate;

    @NotNull(groups = OnCreate.class)
    private BigDecimal platformProviderRate;

    @NotNull(groups = OnCreate.class)
    private BigDecimal platformProviderCap;

    public interface OnCreate {}
    public interface OnUpdate {}

}