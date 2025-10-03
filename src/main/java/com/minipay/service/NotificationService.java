package com.minipay.service;

import org.springframework.stereotype.Service;

/**
 * @author timilehinolowookere
 * @date 9/19/25
 */
public interface NotificationService {
    void notifyUser(String username, String email, String defaultPassword);
}
