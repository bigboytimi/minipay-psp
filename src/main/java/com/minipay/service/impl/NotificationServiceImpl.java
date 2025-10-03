package com.minipay.service.impl;

import com.minipay.service.NotificationService;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {
    @Override
    public void notifyUser(String username, String email, String defaultPassword) {
        return;
    }
}
