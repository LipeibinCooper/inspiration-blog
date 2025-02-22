package com.inspiration.backend.mapper;

import com.inspiration.backend.entity.UserInteraction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface UserInteractionMapper {
    void insert(UserInteraction interaction);
    void delete(@Param("userId") Long userId, @Param("inspirationId") Long inspirationId, @Param("type") UserInteraction.InteractionType type);
    List<UserInteraction> findByUserId(@Param("userId") Long userId);
    List<UserInteraction> findByInspirationId(@Param("inspirationId") Long inspirationId);
    boolean exists(@Param("userId") Long userId, @Param("inspirationId") Long inspirationId, @Param("type") UserInteraction.InteractionType type);
} 