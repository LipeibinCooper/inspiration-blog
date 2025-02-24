package com.inspiration.backend.service;

import com.inspiration.backend.entity.Inspiration;
import com.inspiration.backend.entity.User;

public interface AsyncService {
    void processNewInspiration(Inspiration inspiration);
    void processUserRegistration(User user);
    void processImageUpload(String imageUrl, String directory);
    void sendNotification(Long userId, String message);
} 