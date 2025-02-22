package com.inspiration.backend.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Comment {
    private Long id;
    private String content;
    private Long userId;
    private Long inspirationId;
    private LocalDateTime createdAt;
    
    // 非数据库字段
    private String username;
    private String avatar;
    private String inspirationTitle;
} 