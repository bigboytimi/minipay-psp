package com.minipay.api.authentication.service;

import com.minipay.api.authentication.dto.request.LoginRequest;
import com.minipay.api.authentication.dto.request.RegistrationRequest;
import com.minipay.api.authentication.dto.request.TokenRefreshRequest;
import com.minipay.api.authentication.dto.response.LoginResponse;
import com.minipay.api.authentication.dto.response.RegistrationResponse;
import com.minipay.api.authentication.dto.response.TokenRefreshResponse;
import com.minipay.common.ApiResponse;
import org.springframework.stereotype.Service;

/**
 * @author timilehinolowookere
 * @date 9/19/25
 */
@Service
public interface AuthenticationService {
    LoginResponse login(LoginRequest request);
    TokenRefreshResponse refresh(TokenRefreshRequest request);


}
