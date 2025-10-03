package com.minipay.api.authentication.service.impl;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {
    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCK_TIME_MS = 15 * 60 * 1000;

    private final Map<String, LoginAttempt> attempts = new ConcurrentHashMap<>();

    public void loginSucceeded(String key) {
        attempts.remove(key);
    }

    public void loginFailed(String key) {
        LoginAttempt attempt = attempts.getOrDefault(key, new LoginAttempt(0, Instant.now()));
        attempt.count++;
        attempt.lastAttempt = Instant.now();
        attempts.put(key, attempt);
    }

    public boolean isBlocked(String key) {
        LoginAttempt attempt = attempts.get(key);
        if (attempt == null) return false;
        if (attempt.count >= MAX_ATTEMPTS) {
            return attempt.lastAttempt.plusMillis(LOCK_TIME_MS).isAfter(Instant.now());
        }
        return false;
    }

    private static class LoginAttempt {
        int count;
        Instant lastAttempt;

        LoginAttempt(int count, Instant lastAttempt) {
            this.count = count;
            this.lastAttempt = lastAttempt;
        }
    }
}
