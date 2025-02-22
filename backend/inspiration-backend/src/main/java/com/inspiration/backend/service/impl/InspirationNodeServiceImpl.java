package com.inspiration.backend.service.impl;

import com.inspiration.backend.entity.InspirationNode;
import com.inspiration.backend.entity.NodeImage;
import com.inspiration.backend.mapper.InspirationNodeMapper;
import com.inspiration.backend.mapper.NodeImageMapper;
import com.inspiration.backend.service.InspirationNodeService;
import com.inspiration.backend.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InspirationNodeServiceImpl implements InspirationNodeService {
    
    private final InspirationNodeMapper nodeMapper;
    private final NodeImageMapper imageMapper;

    @Override
    @Transactional
    @CacheEvict(value = {"nodes", "inspiration_nodes", "parent_nodes"}, allEntries = true)
    public InspirationNode create(InspirationNode node) {
        nodeMapper.insert(node);
        return node;
    }

    @Override
    @Cacheable(value = "nodes", key = "#id")
    public InspirationNode findById(Long id) {
        InspirationNode node = nodeMapper.findById(id);
        if (node == null) {
            throw new BusinessException("节点不存在");
        }
        return node;
    }

    @Override
    @Cacheable(value = "inspiration_nodes", key = "#inspirationId")
    public List<InspirationNode> findByInspirationId(Long inspirationId) {
        return nodeMapper.findByInspirationId(inspirationId);
    }

    @Override
    @Cacheable(value = "parent_nodes", key = "#parentId")
    public List<InspirationNode> findByParentId(Long parentId) {
        return nodeMapper.findByParentId(parentId);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"nodes", "inspiration_nodes", "parent_nodes"}, allEntries = true)
    public void update(InspirationNode node) {
        findById(node.getId()); // 检查节点是否存在
        nodeMapper.update(node);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"nodes", "inspiration_nodes", "parent_nodes"}, allEntries = true)
    public void delete(Long id) {
        // 先删除节点的所有图片
        imageMapper.deleteByNodeId(id);
        // 再删除节点
        nodeMapper.delete(id);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"nodes", "inspiration_nodes"}, allEntries = true)
    public void addImage(Long nodeId, String imageUrl, boolean isThumbnail) {
        NodeImage image = new NodeImage();
        image.setNodeId(nodeId);
        image.setImageUrl(imageUrl);
        image.setIsThumbnail(isThumbnail);
        imageMapper.insert(image);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"nodes", "inspiration_nodes"}, allEntries = true)
    public void removeImage(Long nodeId, Long imageId) {
        imageMapper.delete(imageId);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"nodes", "inspiration_nodes"}, allEntries = true)
    public void setThumbnail(Long nodeId, Long imageId) {
        imageMapper.setThumbnail(nodeId, imageId);
    }
} 