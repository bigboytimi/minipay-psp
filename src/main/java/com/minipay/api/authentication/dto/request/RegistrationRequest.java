package com.minipay.api.authentication.dto.request;

import com.minipay.api.authentication.domain.enums.RoleEnum;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * @author timilehinolowookere
 * @date 9/19/25
 */
@Data
public class RegistrationRequest {
    @NotBlank(message = "username must not be empty")
    private String username;
    @NotBlank(message = "email must not be empty")
    private String email;
    @NotBlank(message = "role must not be empty")
    private RoleEnum role;
}
