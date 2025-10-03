package com.minipay.api.merchant.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MerchantStatusUpdateResponse {
    private String merchantId;
    private String status;
}
