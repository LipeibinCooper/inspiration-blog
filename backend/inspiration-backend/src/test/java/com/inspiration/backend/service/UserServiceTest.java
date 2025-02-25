package com.inspiration.backend.service;

import com.inspiration.backend.dto.RegisterRequest;
import com.inspiration.backend.dto.UserDTO;
import com.inspiration.backend.exception.BusinessException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    @Transactional
    public void testRegister() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setPassword("password123");
        request.setConfirmPassword("password123");
//        request.setEmail("test@example.com");

        UserDTO userDTO = userService.register(request);
        assertNotNull(userDTO);
        assertEquals("testuser", userDTO.getUsername());
        assertNotNull(userDTO.getId());
    }

    @Test
    @Transactional
    public void testRegisterWithExistingUsername() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setPassword("password123");
        request.setConfirmPassword("password123");
//        request.setEmail("test@example.com");

        userService.register(request);

        RegisterRequest duplicateRequest = new RegisterRequest();
        duplicateRequest.setUsername("testuser");
        duplicateRequest.setPassword("password456");
        duplicateRequest.setConfirmPassword("password456");
//        duplicateRequest.setEmail("test2@example.com");

        assertThrows(BusinessException.class, () -> userService.register(duplicateRequest));
    }
} 