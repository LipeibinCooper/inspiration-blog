package com.inspiration.backend.service;

import com.inspiration.backend.entity.InspirationNode;
import java.util.List;

public interface InspirationNodeService {
    InspirationNode create(InspirationNode node);
    InspirationNode findById(Long id);
    List<InspirationNode> findByInspirationId(Long inspirationId);
    List<InspirationNode> findByParentId(Long parentId);
    void update(InspirationNode node);
    void delete(Long id);
    void addImage(Long nodeId, String imageUrl, boolean isThumbnail);
    void removeImage(Long nodeId, Long imageId);
    void setThumbnail(Long nodeId, Long imageId);
} 