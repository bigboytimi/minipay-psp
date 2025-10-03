package com.minipay.api.authentication.service;

import com.minipay.api.authentication.domain.User;
import com.minipay.api.authentication.dto.request.RegistrationRequest;
import com.minipay.api.authentication.dto.response.RegistrationResponse;

public interface UserService {
    User getAuthenticatedUser();

    RegistrationResponse register(RegistrationRequest request);

}
