package com.inspiration.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long userId;
    private String content;
    private NotificationType type;
    private Long sourceId;  // 触发通知的源对象ID
    private Long sourceUserId;  // 触发通知的用户ID

    public enum NotificationType {
        LIKE,           // 点赞通知
        COMMENT,        // 评论通知
        COLLECTION,     // 收藏通知
        FOLLOW,         // 关注通知
        SYSTEM         // 系统通知
    }
} 