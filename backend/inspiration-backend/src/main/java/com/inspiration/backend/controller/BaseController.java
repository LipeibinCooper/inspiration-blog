package com.inspiration.backend.controller;

import com.inspiration.backend.model.Result;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public abstract class BaseController {
    
    protected <T> Result<T> success() {
        return Result.success();
    }

    protected <T> Result<T> success(T data) {
        return Result.success(data);
    }

    protected <T> Result<T> success(String message, T data) {
        return Result.success(message, data);
    }

    protected <T> Result<T> error(String message) {
        return Result.error(message);
    }

    protected Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return Long.parseLong(authentication.getName());
        }
        return null;
    }
} 