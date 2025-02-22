package com.inspiration.backend.service;

import com.inspiration.backend.entity.Comment;
import java.util.List;

public interface CommentService {
    Comment create(Comment comment);
    List<Comment> findByInspirationId(Long inspirationId);
    List<Comment> findByUserId(Long userId);
    void delete(Long id, Long userId);
    void deleteByInspirationId(Long inspirationId);
} 