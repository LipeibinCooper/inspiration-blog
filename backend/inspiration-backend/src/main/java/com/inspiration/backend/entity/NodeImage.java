package com.inspiration.backend.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NodeImage {
    private Long id;
    private Long nodeId;
    private String imageUrl;
    private Boolean isThumbnail;
    private LocalDateTime createdAt;
} 