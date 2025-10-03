package com.minipay.api.merchant.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * @author timilehinolowookere
 * @date 9/19/25
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MerchantResponse {
    private String merchantId;
    private String merchantName;
    private String merchantEmail;
    private String status;
    private String settlementAccount;
    private String settlementBank;
    private String callbackUrl;
    private String webhookSecret;
    private ChargeSettingResponse chargeSettingResponse;
}
