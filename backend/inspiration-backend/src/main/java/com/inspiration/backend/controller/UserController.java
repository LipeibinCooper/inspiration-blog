package com.inspiration.backend.controller;

import com.inspiration.backend.dto.UserDTO;
import com.inspiration.backend.model.Result;
import com.inspiration.backend.service.FileService;
import com.inspiration.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "用户管理", description = "用户信息管理接口")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor(onConstructor_ = {@Lazy})

public class UserController extends BaseController {

    private final UserService userService;
    private final FileService fileService;

    @Operation(summary = "获取用户信息")
    @GetMapping("/{id}")
    public Result<UserDTO> getUserById(@Parameter(description = "用户ID") @PathVariable Long id) {
        return success(userService.findById(id));
    }

    @Operation(summary = "更新用户资料")
    @PutMapping("/profile")
    public Result<UserDTO> updateProfile(@Valid @RequestBody UserDTO userDTO) {
        return success(userService.updateProfile(getCurrentUserId(), userDTO));
    }

    @Operation(summary = "更新用户头像")
    @PostMapping("/avatar")
    public Result<?> updateAvatar(@Parameter(description = "头像文件") @RequestParam("file") MultipartFile file) {
        String avatarUrl = fileService.uploadFile(file, "avatars");
        userService.updateAvatar(getCurrentUserId(), avatarUrl);
        return success();
    }

    @Operation(summary = "更新用户简介")
    @PutMapping("/bio")
    public Result<?> updateBio(@Parameter(description = "用户简介") @RequestParam String bio) {
        userService.updateBio(getCurrentUserId(), bio);
        return success();
    }
} 