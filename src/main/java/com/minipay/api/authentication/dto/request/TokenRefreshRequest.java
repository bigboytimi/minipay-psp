package com.minipay.api.authentication.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author timilehinolowookere
 * @date 9/19/25
 */

@Data
public class TokenRefreshRequest {
    @NotBlank
    private String refreshToken;
}
