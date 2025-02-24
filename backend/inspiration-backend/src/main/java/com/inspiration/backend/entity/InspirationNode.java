package com.inspiration.backend.entity;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class InspirationNode {
    private Long id;
    private Long inspirationId;
    private Long parentId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 非数据库字段
    private List<String> imageUrls;
    private String thumbnailUrl;
} 