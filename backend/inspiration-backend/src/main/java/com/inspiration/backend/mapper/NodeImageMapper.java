package com.inspiration.backend.mapper;

import com.inspiration.backend.entity.NodeImage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface NodeImageMapper {
    void insert(NodeImage image);
    List<NodeImage> findByNodeId(@Param("nodeId") Long nodeId);
    void delete(@Param("id") Long id);
    void deleteByNodeId(@Param("nodeId") Long nodeId);
    void setThumbnail(@Param("nodeId") Long nodeId, @Param("imageId") Long imageId);
} 