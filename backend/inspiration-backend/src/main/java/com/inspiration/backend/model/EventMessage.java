package com.inspiration.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    private EventType type;
    private Long userId;
    private Long targetId;
    private String data;
    private LocalDateTime timestamp;

    public enum EventType {
        NEW_INSPIRATION,    // 新灵感创建
        NEW_COMMENT,        // 新评论
        USER_INTERACTION,   // 用户互动（点赞、收藏等）
        USER_REGISTER,      // 用户注册
        IMAGE_UPLOAD       // 图片上传
    }
} 