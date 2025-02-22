package com.inspiration.backend.service;

import com.inspiration.backend.entity.User;
import com.inspiration.backend.dto.UserDTO;
import com.inspiration.backend.dto.LoginRequest;
import com.inspiration.backend.dto.RegisterRequest;

public interface UserService {
    UserDTO login(LoginRequest loginRequest);
    UserDTO register(RegisterRequest registerRequest);
    UserDTO findById(Long id);
    UserDTO updateProfile(Long userId, UserDTO userDTO);
    void updateAvatar(Long userId, String avatarUrl);
    void updateBio(Long userId, String bio);
} 