package com.minipay.api.authentication.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author timilehinolowookere
 * @date 9/19/25
 */
@Data
public class LoginRequest {
    @NotBlank(message = "username must not be empty")
    private String username;
    @NotBlank(message = "password must not be empty")
    private String password;
}
