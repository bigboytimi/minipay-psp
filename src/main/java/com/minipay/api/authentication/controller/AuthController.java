package com.minipay.api.authentication.controller;

import com.minipay.api.ApiConstants;
import com.minipay.api.authentication.dto.request.LoginRequest;
import com.minipay.api.authentication.dto.request.RegistrationRequest;
import com.minipay.api.authentication.dto.request.TokenRefreshRequest;
import com.minipay.api.authentication.dto.response.LoginResponse;
import com.minipay.api.authentication.dto.response.RegistrationResponse;
import com.minipay.api.authentication.dto.response.TokenRefreshResponse;
import com.minipay.api.authentication.service.AuthenticationService;
import com.minipay.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.minipay.api.ApiConstants.BASE_PATH;

/**
 * @author timilehinolowookere
 * @date 9/19/25
 */

@RequestMapping(BASE_PATH)
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
@RestController
@Tag(name = "Authentication", description = "Authentication operations API")
public class AuthController extends ApiConstants {

    private final AuthenticationService authenticationService;

    @PostMapping(value = REGISTER)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Register User", description = "Creates a new user")
    public ResponseEntity<ApiResponse<RegistrationResponse>> register(@RequestBody @Valid RegistrationRequest request){
        log.info("initiating user registration...");
        RegistrationResponse response = authenticationService.register(request);
        return ApiResponse.success(response, "registration completed");
    }

    @PostMapping(value = LOGIN)
    @Operation(summary = "Login User", description = "Login an existing user and generate token")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody @Valid LoginRequest request){
        log.info("processing login...");
        LoginResponse response = authenticationService.login(request);
        return ApiResponse.success(response, "login successful");
    }

    @PostMapping(value = REFRESH)
    @Operation(summary = "Refresh Token", description = "Refresh access token")
    public ResponseEntity<ApiResponse<TokenRefreshResponse>> refresh(@RequestBody @Valid TokenRefreshRequest request){
        log.info("initiating token refresh...");
        TokenRefreshResponse response = authenticationService.refresh(request);
        return ApiResponse.success(response, "refresh token generated");
    }
}
