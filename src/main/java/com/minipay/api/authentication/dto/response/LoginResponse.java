package com.minipay.api.authentication.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author timilehinolowookere
 * @date 9/19/25
 */
@Data
@AllArgsConstructor
public class LoginResponse {
    private String refreshToken;
    private String accessToken;
}
