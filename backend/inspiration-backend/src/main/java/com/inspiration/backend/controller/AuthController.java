package com.inspiration.backend.controller;

import com.inspiration.backend.dto.LoginRequest;
import com.inspiration.backend.dto.RegisterRequest;
import com.inspiration.backend.dto.UserDTO;
import com.inspiration.backend.model.Result;
import com.inspiration.backend.security.JwtTokenProvider;
import com.inspiration.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "认证管理", description = "用户登录注册相关接口")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController extends BaseController {

    private final UserService userService;
    private final JwtTokenProvider tokenProvider;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginRequest loginRequest) {
        UserDTO userDTO = userService.login(loginRequest);
        
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDTO.getId().toString(), null);
        String token = tokenProvider.generateToken(authentication);

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", userDTO);
        
        return success(response);
    }

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<UserDTO> register(@Valid @RequestBody RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("两次输入的密码不一致");
        }
        return success(userService.register(request));
    }

    @Operation(summary = "获取当前用户信息")
    @GetMapping("/me")
    public Result<UserDTO> getCurrentUser() {
        Long userId = getCurrentUserId();
        return success(userService.findById(userId));
    }
} 