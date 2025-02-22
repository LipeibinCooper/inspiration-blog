package com.inspiration.backend.message;

import com.inspiration.backend.config.RabbitConfig;
import com.inspiration.backend.service.AsyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageConsumer {
    // ... 现有代码 ...
} 