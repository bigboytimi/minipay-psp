package com.minipay.api.merchant.dto.request;

import com.minipay.api.authentication.domain.enums.UserStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author timilehinolowookere
 * @date 9/19/25
 */
@Data
public class MerchantUpdateRequest {
    private String merchantName;
    private String merchantEmail;
    private String settlementAccount;
    private String settlementBank;
    private String callbackUrl;
    private ChargeSettingRequest chargeSettingRequest;
}
