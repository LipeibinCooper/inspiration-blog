package com.inspiration.backend.service.impl;

import com.inspiration.backend.entity.Comment;
import com.inspiration.backend.mapper.CommentMapper;

import com.inspiration.backend.mapper.InspirationMapper;
import com.inspiration.backend.model.EventMessage;
import com.inspiration.backend.model.NotificationMessage;
import com.inspiration.backend.service.CommentService;
import com.inspiration.backend.exception.BusinessException;
import com.inspiration.backend.service.MessageProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
//import com.inspiration.backend.message.EventMessage;
//import com.inspiration.backend.message.NotificationMessage;
//import com.inspiration.backend.message.MessageProducer;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
public class CommentServiceImpl implements CommentService {
    
    private final CommentMapper commentMapper;
    private final MessageProducer messageProducer;

    private final InspirationMapper inspirationMapper;

    @Override
    @Transactional
    @CacheEvict(value = {"inspiration_comments", "user_comments"}, allEntries = true)
    public Comment create(Comment comment) {
        commentMapper.insert(comment);

        // 发送通知消息
        messageProducer.sendNotification(NotificationMessage.builder()
                .userId(inspirationMapper.findById(comment.getInspirationId()).getUserId())
                .content("有人评论了你的灵感")
                .type(NotificationMessage.NotificationType.COMMENT)
                .sourceId(comment.getId())
                .sourceUserId(comment.getUserId())
                .build());

        // 发送事件消息
        messageProducer.sendEvent(EventMessage.builder()
                .type(EventMessage.EventType.NEW_COMMENT)
                .userId(comment.getUserId())
                .targetId(comment.getInspirationId())
                .data(comment.getContent())
                .timestamp(LocalDateTime.now())
                .build());

        return comment;
    }

    @Override
    @Cacheable(value = "inspiration_comments", key = "#inspirationId")
    public List<Comment> findByInspirationId(Long inspirationId) {
        return commentMapper.findByInspirationId(inspirationId);
    }

    @Override
    @Cacheable(value = "user_comments", key = "#userId")
    public List<Comment> findByUserId(Long userId) {
        return commentMapper.findByUserId(userId);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"inspiration_comments", "user_comments"}, allEntries = true)
    public void delete(Long id, Long userId) {
        List<Comment> comments = commentMapper.findByUserId(userId);
        boolean isOwner = comments.stream()
                .anyMatch(comment -> comment.getId().equals(id));
        if (!isOwner) {
            throw new BusinessException("无权删除他人的评论");
        }
        commentMapper.delete(id);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"inspiration_comments", "user_comments"}, allEntries = true)
    public void deleteByInspirationId(Long inspirationId) {
        commentMapper.deleteByInspirationId(inspirationId);
    }
} 