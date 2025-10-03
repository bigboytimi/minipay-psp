package com.minipay.api.authentication.service.impl;

import com.minipay.api.authentication.domain.Role;
import com.minipay.api.authentication.domain.User;
import com.minipay.api.authentication.domain.enums.UserStatus;
import com.minipay.api.authentication.dto.request.RegistrationRequest;
import com.minipay.api.authentication.dto.response.RegistrationResponse;
import com.minipay.api.authentication.repository.RoleRepository;
import com.minipay.api.authentication.repository.UserRepository;
import com.minipay.api.authentication.service.UserService;
import com.minipay.api.merchant.domain.Merchant;
import com.minipay.api.merchant.service.MerchantService;
import com.minipay.common.ResponseCode;
import com.minipay.exception.ApiException;
import com.minipay.service.NotificationService;
import com.minipay.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MerchantService merchantService;
    private final RoleRepository roleRepository;
    private final NotificationService notificationService;

    @Override
    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            return userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        } else {
            return null;
        }
    }

    @Override
    public RegistrationResponse register(RegistrationRequest request) {
        log.info("initiating user registration...");

        if (userRepository.existsByEmailOrUsername(request.getEmail(), request.getUsername())) {
            throw new ApiException("Email or username already exists", ResponseCode.DUPLICATE.getCode());
        }

        Merchant merchant = merchantService.getMerchantById(request.getMerchantId());
        String defaultPassword = SecurityUtil.generatePassword(10, 15, true);

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setStatus(UserStatus.ACTIVE);
        user.setMerchant(merchant);

        user.setPasswordHash(passwordEncoder.encode(defaultPassword));

        RegistrationResponse response = new RegistrationResponse();

        try {
            Role role = roleRepository.findByName(request.getRole())
                    .orElseThrow(() -> new ApiException("Role not found"));

            user.getRoles().add(role);
            User savedUser = userRepository.save(user);

            response.setUserId(savedUser.getId());
            response.setEmail(savedUser.getEmail());
            response.setUserName(savedUser.getUsername());

            notificationService.notifyUser(savedUser.getUsername(), savedUser.getEmail(), defaultPassword);

            return response;

        } catch (Exception e) {
            log.info("exception occurred while registering user...");
            throw new ApiException("Error occurred on registration", ResponseCode.SYSTEM_MALFUNCTION.getCode());
        }
    }
}
