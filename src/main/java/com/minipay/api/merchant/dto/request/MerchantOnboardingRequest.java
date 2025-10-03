package com.minipay.api.merchant.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author timilehinolowookere
 * @date 9/19/25
 */
@Data
public class MerchantOnboardingRequest {
    @NotBlank(message = "merchantName must not be empty")
    private String merchantName;
    @NotBlank(message = "merchantEmail must not be empty")
    private String merchantEmail;
    @NotBlank(message = "settlementAccount must not be empty")
    private String settlementAccount;
    @NotBlank(message = "settlementBank must not be empty")
    private String settlementBank;
    @NotBlank(message = "callbackUrl must not be empty")
    private String callbackUrl;
    @NotNull
    private ChargeSettingRequest chargeSettingRequest;
}
