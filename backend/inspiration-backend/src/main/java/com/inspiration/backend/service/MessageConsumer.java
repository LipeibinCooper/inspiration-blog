package com.inspiration.backend.service;

import com.inspiration.backend.model.NotificationMessage;
import com.inspiration.backend.model.EventMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import com.inspiration.backend.config.RabbitConfig;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageConsumer {

    private final AsyncService asyncService;

    @RabbitListener(queues = RabbitConfig.NOTIFICATION_QUEUE)
    public void handleNotification(NotificationMessage message) {
        log.info("接收到通知消息: {}", message);
        try {
            asyncService.sendNotification(message.getUserId(), message.getContent());
        } catch (Exception e) {
            log.error("处理通知消息时发生错误", e);
        }
    }

    @RabbitListener(queues = RabbitConfig.EVENT_QUEUE)
    public void handleEvent(EventMessage message) {
        log.info("接收到事件消息: {}", message);
        try {
            // TODO: 根据事件类型处理不同的业务逻辑
            switch (message.getType()) {
                case NEW_INSPIRATION:
                    // 处理新灵感事件
                    break;
                case NEW_COMMENT:
                    // 处理新评论事件
                    break;
                case USER_INTERACTION:
                    // 处理用户互动事件
                    break;
                default:
                    log.warn("未知的事件类型: {}", message.getType());
            }
        } catch (Exception e) {
            log.error("处理事件消息时发生错误", e);
        }
    }
} 