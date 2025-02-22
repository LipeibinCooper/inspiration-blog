package com.inspiration.backend.mapper;

import com.inspiration.backend.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface CommentMapper {
    void insert(Comment comment);
    List<Comment> findByInspirationId(@Param("inspirationId") Long inspirationId);
    List<Comment> findByUserId(@Param("userId") Long userId);
    void delete(@Param("id") Long id);
    void deleteByInspirationId(@Param("inspirationId") Long inspirationId);
} 