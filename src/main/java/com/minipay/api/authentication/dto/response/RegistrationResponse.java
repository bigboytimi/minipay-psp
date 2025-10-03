package com.minipay.api.authentication.dto.response;

import lombok.Data;

/**
 * @author timilehinolowookere
 * @date 9/19/25
 */
@Data
public class RegistrationResponse {
    private String userId;
    private String userName;
    private String email;
}
