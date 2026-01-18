package com.rodrigocoelhoo.lifemanager.config;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component("userAwareKeyGenerator")
public class UserAwareKeyGenerator implements KeyGenerator {

    @Override
    public Object generate(Object target, Method method, Object... params) {
        String username = getCurrentUsername();
        StringBuilder key = new StringBuilder(username);

        key.append(":").append(method.getName());

        for (Object param : params) {
            if (param != null) {
                key.append(":").append(param.toString());
            }
        }

        return key.toString();
    }

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "anonymous";
    }
}