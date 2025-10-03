package com.minipay.api.authentication.service;

import com.minipay.api.authentication.domain.User;

public interface UserService {
    User getAuthenticatedUser();

}
