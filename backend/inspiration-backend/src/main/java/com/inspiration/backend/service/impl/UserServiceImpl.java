package com.inspiration.backend.service.impl;

import com.inspiration.backend.entity.User;
import com.inspiration.backend.dto.UserDTO;
import com.inspiration.backend.dto.LoginRequest;
import com.inspiration.backend.dto.RegisterRequest;
import com.inspiration.backend.mapper.UserMapper;

import com.inspiration.backend.model.EventMessage;
import com.inspiration.backend.model.NotificationMessage;
import com.inspiration.backend.service.MessageProducer;
import com.inspiration.backend.service.UserService;
import com.inspiration.backend.exception.BusinessException;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

//import com.inspiration.backend.message.EventMessage;
//import com.inspiration.backend.message.NotificationMessage;
//import com.inspiration.backend.message.MessageProducer;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})

public class UserServiceImpl implements UserService {
    
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final MessageProducer messageProducer;

    @Override
    public UserDTO login(LoginRequest loginRequest) {
        User user = userMapper.findByUsername(loginRequest.getUsername());
        if (user == null || !passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        return convertToDTO(user);
    }

    @Override
    @Transactional
    public UserDTO register(RegisterRequest registerRequest) {
        // 验证用户名是否已存在
        if (userMapper.findByUsername(registerRequest.getUsername()) != null) {
            throw new BusinessException("用户名已存在");
        }

        // 验证两次密码是否一致
        if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
            throw new BusinessException("两次输入的密码不一致");
        }

        // 创建新用户
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        userMapper.insert(user);

        // 发送用户注册事件
        messageProducer.sendEvent(EventMessage.builder()
                .type(EventMessage.EventType.USER_REGISTER)
                .userId(user.getId())
                .timestamp(LocalDateTime.now())
                .build());

        // 发送系统欢迎通知
        messageProducer.sendNotification(NotificationMessage.builder()
                .userId(user.getId())
                .content("欢迎加入灵感分享平台！")
                .type(NotificationMessage.NotificationType.SYSTEM)
                .build());

        return convertToDTO(user);
    }

    @Cacheable(value = "users", key = "#id")
    @Override
    public UserDTO findById(Long id) {
        User user = userMapper.findById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return convertToDTO(user);
    }

    @CacheEvict(value = "users", key = "#userId")
    @Override
    @Transactional
    public UserDTO updateProfile(Long userId, UserDTO userDTO) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 如果修改用户名，需要验证是否已存在
        if (!user.getUsername().equals(userDTO.getUsername())) {
            if (userMapper.findByUsername(userDTO.getUsername()) != null) {
                throw new BusinessException("用户名已存在");
            }
        }

        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setBio(userDTO.getBio());
        userMapper.update(user);

        return convertToDTO(user);
    }

    @CacheEvict(value = "users", key = "#userId")
    @Override
    @Transactional
    public void updateAvatar(Long userId, String avatarUrl) {
        userMapper.updateAvatar(userId, avatarUrl);
    }

    @CacheEvict(value = "users", key = "#userId")
    @Override
    @Transactional
    public void updateBio(Long userId, String bio) {
        userMapper.updateBio(userId, bio);
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        BeanUtils.copyProperties(user, dto);
        return dto;
    }
} 