package com.minipay.api.authentication.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author timilehinolowookere
 * @date 9/19/25
 */

@Data
@AllArgsConstructor
public class TokenRefreshResponse {
    private String token;
    private String refreshToken;
}
