package com.inspiration.backend.service.impl;

import com.inspiration.backend.entity.UserInteraction;
import com.inspiration.backend.mapper.UserInteractionMapper;

import com.inspiration.backend.model.EventMessage;
import com.inspiration.backend.model.NotificationMessage;
import com.inspiration.backend.service.MessageProducer;
import com.inspiration.backend.service.UserInteractionService;
import com.inspiration.backend.service.InspirationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
//import com.inspiration.backend.message.EventMessage;
//import com.inspiration.backend.message.NotificationMessage;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})

public class UserInteractionServiceImpl implements UserInteractionService {
    
    private final UserInteractionMapper interactionMapper;
    private final InspirationService inspirationService;
    private final MessageProducer messageProducer;

    @Override
    @Transactional
    @CacheEvict(value = {"user_interactions", "inspiration_interactions"}, allEntries = true)
    public void toggleLike(Long userId, Long inspirationId) {
        boolean exists = interactionMapper.exists(userId, inspirationId, UserInteraction.InteractionType.LIKE);
        
        if (!exists) {
            UserInteraction interaction = new UserInteraction();
            interaction.setUserId(userId);
            interaction.setInspirationId(inspirationId);
            interaction.setType(UserInteraction.InteractionType.LIKE);
            interactionMapper.insert(interaction);

            // 发送通知消息
            messageProducer.sendNotification(NotificationMessage.builder()
                    .userId(inspirationService.findById(inspirationId).getUserId())
                    .content("有人点赞了你的灵感")
                    .type(NotificationMessage.NotificationType.LIKE)
                    .sourceId(inspirationId)
                    .sourceUserId(userId)
                    .build());
        } else {
            interactionMapper.delete(userId, inspirationId, UserInteraction.InteractionType.LIKE);
        }

        // 发送事件消息
        messageProducer.sendEvent(EventMessage.builder()
                .type(EventMessage.EventType.USER_INTERACTION)
                .userId(userId)
                .targetId(inspirationId)
                .data("like:" + !exists)
                .timestamp(LocalDateTime.now())
                .build());

        // 更新灵感的统计数据
        List<UserInteraction> interactions = interactionMapper.findByInspirationId(inspirationId);
        long likes = interactions.stream()
                .filter(i -> i.getType() == UserInteraction.InteractionType.LIKE)
                .count();
        long collections = interactions.stream()
                .filter(i -> i.getType() == UserInteraction.InteractionType.COLLECTION)
                .count();
        
        inspirationService.updateStats(inspirationId, (int)likes, (int)collections);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"user_interactions", "inspiration_interactions"}, allEntries = true)
    public void toggleCollection(Long userId, Long inspirationId) {
        boolean exists = interactionMapper.exists(userId, inspirationId, UserInteraction.InteractionType.COLLECTION);
        
        if (!exists) {
            UserInteraction interaction = new UserInteraction();
            interaction.setUserId(userId);
            interaction.setInspirationId(inspirationId);
            interaction.setType(UserInteraction.InteractionType.COLLECTION);
            interactionMapper.insert(interaction);

            // 发送通知消息
            messageProducer.sendNotification(NotificationMessage.builder()
                    .userId(inspirationService.findById(inspirationId).getUserId())
                    .content("有人收藏了你的灵感")
                    .type(NotificationMessage.NotificationType.COLLECTION)
                    .sourceId(inspirationId)
                    .sourceUserId(userId)
                    .build());
        } else {
            interactionMapper.delete(userId, inspirationId, UserInteraction.InteractionType.COLLECTION);
        }

        // 发送事件消息
        messageProducer.sendEvent(EventMessage.builder()
                .type(EventMessage.EventType.USER_INTERACTION)
                .userId(userId)
                .targetId(inspirationId)
                .data("collection:" + !exists)
                .timestamp(LocalDateTime.now())
                .build());

        // 更新灵感的统计数据
        List<UserInteraction> interactions = interactionMapper.findByInspirationId(inspirationId);
        long likes = interactions.stream()
                .filter(i -> i.getType() == UserInteraction.InteractionType.LIKE)
                .count();
        long collections = interactions.stream()
                .filter(i -> i.getType() == UserInteraction.InteractionType.COLLECTION)
                .count();
        
        inspirationService.updateStats(inspirationId, (int)likes, (int)collections);
    }

    @Override
    @Cacheable(value = "user_interactions", key = "#userId")
    public List<UserInteraction> findByUserId(Long userId) {
        return interactionMapper.findByUserId(userId);
    }

    @Override
    @Cacheable(value = "inspiration_interactions", key = "#inspirationId")
    public List<UserInteraction> findByInspirationId(Long inspirationId) {
        return interactionMapper.findByInspirationId(inspirationId);
    }

    @Override
    @Cacheable(value = "user_interaction_status", key = "'like:' + #userId + ':' + #inspirationId")
    public boolean hasLiked(Long userId, Long inspirationId) {
        return interactionMapper.exists(userId, inspirationId, UserInteraction.InteractionType.LIKE);
    }

    @Override
    @Cacheable(value = "user_interaction_status", key = "'collection:' + #userId + ':' + #inspirationId")
    public boolean hasCollected(Long userId, Long inspirationId) {
        return interactionMapper.exists(userId, inspirationId, UserInteraction.InteractionType.COLLECTION);
    }
} 