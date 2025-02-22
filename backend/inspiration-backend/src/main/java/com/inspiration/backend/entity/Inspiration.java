package com.inspiration.backend.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Inspiration {
    private Long id;
    private String title;
    private String content;
    private Long userId;
    private Boolean isPublic;
    private Integer likes;
    private Integer collections;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 非数据库字段
    private User author;
} 