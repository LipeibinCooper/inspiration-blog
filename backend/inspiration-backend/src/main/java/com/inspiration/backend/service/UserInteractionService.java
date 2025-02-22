package com.inspiration.backend.service;

import com.inspiration.backend.entity.UserInteraction;
import java.util.List;

public interface UserInteractionService {
    void toggleLike(Long userId, Long inspirationId);
    void toggleCollection(Long userId, Long inspirationId);
    List<UserInteraction> findByUserId(Long userId);
    List<UserInteraction> findByInspirationId(Long inspirationId);
    boolean hasLiked(Long userId, Long inspirationId);
    boolean hasCollected(Long userId, Long inspirationId);
} 