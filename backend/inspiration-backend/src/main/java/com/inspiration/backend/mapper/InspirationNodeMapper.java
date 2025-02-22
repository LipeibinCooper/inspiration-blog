package com.inspiration.backend.mapper;

import com.inspiration.backend.entity.InspirationNode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface InspirationNodeMapper {
    void insert(InspirationNode node);
    InspirationNode findById(@Param("id") Long id);
    List<InspirationNode> findByInspirationId(@Param("inspirationId") Long inspirationId);
    List<InspirationNode> findByParentId(@Param("parentId") Long parentId);
    void update(InspirationNode node);
    void delete(@Param("id") Long id);
} 