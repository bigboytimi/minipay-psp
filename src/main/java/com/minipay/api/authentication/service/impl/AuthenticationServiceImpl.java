package com.minipay.api.authentication.service.impl;

import com.minipay.api.authentication.domain.Role;
import com.minipay.api.authentication.domain.User;
import com.minipay.api.authentication.domain.enums.UserStatus;
import com.minipay.api.authentication.dto.request.LoginRequest;
import com.minipay.api.authentication.dto.request.RegistrationRequest;
import com.minipay.api.authentication.dto.request.TokenRefreshRequest;
import com.minipay.api.authentication.dto.response.LoginResponse;
import com.minipay.api.authentication.dto.response.RegistrationResponse;
import com.minipay.api.authentication.dto.response.TokenRefreshResponse;
import com.minipay.api.authentication.repository.RoleRepository;
import com.minipay.api.authentication.repository.UserRepository;
import com.minipay.api.authentication.service.AuthenticationService;
import com.minipay.api.merchant.domain.Merchant;
import com.minipay.api.merchant.service.MerchantService;
import com.minipay.common.ResponseCode;
import com.minipay.exception.ApiException;
import com.minipay.security.config.CustomUserDetails;
import com.minipay.security.jwt.JwtService;
import com.minipay.service.NotificationService;
import com.minipay.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author timilehinolowookere
 * @date 9/19/25
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;


    @Override
    public LoginResponse login(LoginRequest request) {
        log.info("initiating user login...");

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ApiException("User does not exist"));

        if (UserStatus.SUSPENDED.equals(user.getStatus())) {
            throw new ApiException("User has been suspended");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            log.info("user input invalid password");
            throw new ApiException("Invalid credentials");
        }

        UserDetails userDetails = new CustomUserDetails(user);

        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        return new LoginResponse(accessToken, refreshToken);
    }

    @Override
    public TokenRefreshResponse refresh(TokenRefreshRequest request) {
        String refreshToken = request.getRefreshToken();

        if (!jwtService.isRefreshToken(refreshToken) || jwtService.extractExpiration(refreshToken).before(new Date())) {
            throw new ApiException("Invalid or expired refresh token");
        }

        String username = jwtService.extractUsername(refreshToken);

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        String newAccessToken = jwtService.generateAccessToken(userDetails);

        return new TokenRefreshResponse(newAccessToken, refreshToken);
    }


}
