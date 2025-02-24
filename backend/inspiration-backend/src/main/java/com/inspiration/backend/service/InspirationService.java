package com.inspiration.backend.service;

import com.inspiration.backend.entity.Inspiration;
import java.util.List;

public interface InspirationService {
    Inspiration create(Inspiration inspiration);
    Inspiration findById(Long id);
    List<Inspiration> findByUserId(Long userId);
    List<Inspiration> findPublic();
    void update(Inspiration inspiration);
    void delete(Long id);
    void updateStats(Long id, Integer likes, Integer collections);
} 