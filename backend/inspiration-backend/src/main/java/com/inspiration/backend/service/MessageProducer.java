package com.inspiration.backend.service;

import com.inspiration.backend.config.RabbitConfig;
import com.inspiration.backend.model.NotificationMessage;
import com.inspiration.backend.model.EventMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendNotification(NotificationMessage message) {
        log.info("发送通知消息: {}", message);
        rabbitTemplate.convertAndSend(
            RabbitConfig.NOTIFICATION_EXCHANGE,
            RabbitConfig.NOTIFICATION_ROUTING_KEY,
            message
        );
    }

    public void sendEvent(EventMessage message) {
        log.info("发送事件消息: {}", message);
        rabbitTemplate.convertAndSend(
            RabbitConfig.EVENT_EXCHANGE,
            RabbitConfig.EVENT_ROUTING_KEY,
            message
        );
    }
} 