package com.inspiration.backend.mapper;

import com.inspiration.backend.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    User findById(@Param("id") Long id);
    User findByUsername(@Param("username") String username);
    void insert(User user);
    void update(User user);
    void updateAvatar(@Param("id") Long id, @Param("avatar") String avatar);
    void updateBio(@Param("id") Long id, @Param("bio") String bio);
} 