package net.fina.security.impl;

import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Singleton
@Startup
@Lock(LockType.READ)
public class LoginAttemptPolicyManager {
    private final Map<String, Integer> loginErrors = new ConcurrentHashMap<>();

    public Integer get(String key) {
        return loginErrors.get(key.trim().toLowerCase());
    }

    public void put(String key, int loginAttempt) {
        loginErrors.put(key.trim().toLowerCase(), loginAttempt);
    }

    public void remove(String key) {
        loginErrors.remove(key.trim().toLowerCase());
    }
}
