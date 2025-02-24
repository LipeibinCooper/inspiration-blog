package com.inspiration.backend.service.impl;

import com.inspiration.backend.entity.Inspiration;
import com.inspiration.backend.mapper.InspirationMapper;
import com.inspiration.backend.service.InspirationService;
import com.inspiration.backend.exception.BusinessException;
import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Lazy;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import com.inspiration.backend.service.AsyncService;
import com.inspiration.backend.model.EventMessage;
import com.inspiration.backend.service.MessageProducer;
import java.time.LocalDateTime;

@Service

@RequiredArgsConstructor(onConstructor_ = {@Lazy})

public class InspirationServiceImpl implements InspirationService {
    
    private final InspirationMapper inspirationMapper;
    private final AsyncService asyncService;
    private final MessageProducer messageProducer;

    @Override
    @Transactional
    public Inspiration create(Inspiration inspiration) {
        inspirationMapper.insert(inspiration);
        // 异步处理新灵感
        asyncService.processNewInspiration(inspiration);
        // 发送事件消息
        messageProducer.sendEvent(EventMessage.builder()
                .type(EventMessage.EventType.NEW_INSPIRATION)
                .userId(inspiration.getUserId())
                .targetId(inspiration.getId())
                .timestamp(LocalDateTime.now())
                .build());
        return inspiration;
    }

    @Cacheable(value = "inspirations", key = "#id")
    @Override
    public Inspiration findById(Long id) {
        Inspiration inspiration = inspirationMapper.findById(id);
        if (inspiration == null) {
            throw new BusinessException("灵感不存在");
        }
        return inspiration;
    }

    @Cacheable(value = "user_inspirations", key = "#userId")
    @Override
    public List<Inspiration> findByUserId(Long userId) {
        return inspirationMapper.findByUserId(userId);
    }

    @Cacheable(value = "public_inspirations")
    @Override
    public List<Inspiration> findPublic() {
        return inspirationMapper.findPublic();
    }

    @CacheEvict(value = {"inspirations", "user_inspirations", "public_inspirations"}, allEntries = true)
    @Override
    @Transactional
    public void update(Inspiration inspiration) {
        Inspiration existing = findById(inspiration.getId());
        if (!existing.getUserId().equals(inspiration.getUserId())) {
            throw new BusinessException("无权修改他人的灵感");
        }
        inspirationMapper.update(inspiration);
    }

    @CacheEvict(value = {"inspirations", "user_inspirations", "public_inspirations"}, allEntries = true)
    @Override
    @Transactional
    public void delete(Long id) {
        inspirationMapper.delete(id);
    }

    @Override
    @Transactional
    public void updateStats(Long id, Integer likes, Integer collections) {
        inspirationMapper.updateStats(id, likes, collections);
    }
} 