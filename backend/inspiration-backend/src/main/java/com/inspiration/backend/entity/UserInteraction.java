package com.inspiration.backend.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserInteraction {
    private Long id;
    private Long userId;
    private Long inspirationId;
    private InteractionType type;
    private LocalDateTime createdAt;
    
    public enum InteractionType {
        LIKE, COLLECTION
    }
} 