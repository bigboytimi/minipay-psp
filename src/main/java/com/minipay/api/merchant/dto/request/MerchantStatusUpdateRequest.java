package com.minipay.api.merchant.dto.request;

import com.minipay.api.authentication.domain.enums.UserStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MerchantStatusUpdateRequest {
    @NotNull(message = "please provide a valid user status.")
    private UserStatus status;
}
