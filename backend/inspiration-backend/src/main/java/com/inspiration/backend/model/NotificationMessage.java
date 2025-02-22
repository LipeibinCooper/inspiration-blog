package com.inspiration.backend.model;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class NotificationMessage {
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