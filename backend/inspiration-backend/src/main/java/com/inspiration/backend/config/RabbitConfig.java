package com.inspiration.backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
//public class RabbitConfig {
//
//    public static final String NOTIFICATION_QUEUE = "notification.queue";
//    public static final String NOTIFICATION_EXCHANGE = "notification.exchange";
//    public static final String NOTIFICATION_ROUTING_KEY = "notification.routing.key";
//
//    public static final String EVENT_QUEUE = "event.queue";
//    public static final String EVENT_EXCHANGE = "event.exchange";
//    public static final String EVENT_ROUTING_KEY = "event.routing.key";
//
//    @Bean
//    public Queue notificationQueue() {
//        return new Queue(NOTIFICATION_QUEUE, true);
//    }
//
//    @Bean
//    public DirectExchange notificationExchange() {
//        return new DirectExchange(NOTIFICATION_EXCHANGE);
//    }
//
//    @Bean
//    public Binding notificationBinding() {
//        return BindingBuilder.bind(notificationQueue())
//                .to(notificationExchange())
//                .with(NOTIFICATION_ROUTING_KEY);
//    }
//
//    @Bean
//    public Queue eventQueue() {
//        return new Queue(EVENT_QUEUE, true);
//    }
//
//    @Bean
//    public DirectExchange eventExchange() {
//        return new DirectExchange(EVENT_EXCHANGE);
//    }
//
//    @Bean
//    public Binding eventBinding() {
//        return BindingBuilder.bind(eventQueue())
//                .to(eventExchange())
//                .with(EVENT_ROUTING_KEY);
//    }
//
//    @Bean
//    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
//        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
//        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
//
//        return rabbitTemplate;
//    }
//    @Bean
//    public MessageConverter messageConverter(ObjectMapper objectMapper) {
//        return new Jackson2JsonMessageConverter(objectMapper);
//    }
//}


@Configuration
public class RabbitConfig {

    public static final String NOTIFICATION_QUEUE = "notification.queue";
    public static final String NOTIFICATION_EXCHANGE = "notification.exchange";
    public static final String NOTIFICATION_ROUTING_KEY = "notification.routing.key";

    public static final String EVENT_QUEUE = "event.queue";
    public static final String EVENT_EXCHANGE = "event.exchange";
    public static final String EVENT_ROUTING_KEY = "event.routing.key";

    @Bean
    public Queue notificationQueue() {
        return new Queue(NOTIFICATION_QUEUE, true);
    }

    @Bean
    public DirectExchange notificationExchange() {
        return new DirectExchange(NOTIFICATION_EXCHANGE);
    }

    @Bean
    public Binding notificationBinding() {
        return BindingBuilder.bind(notificationQueue())
                .to(notificationExchange())
                .with(NOTIFICATION_ROUTING_KEY);
    }

    @Bean
    public Queue eventQueue() {
        return new Queue(EVENT_QUEUE, true);
    }

    @Bean
    public DirectExchange eventExchange() {
        return new DirectExchange(EVENT_EXCHANGE);
    }

    @Bean
    public Binding eventBinding() {
        return BindingBuilder.bind(eventQueue())
                .to(eventExchange())
                .with(EVENT_ROUTING_KEY);
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    @Bean
    public MessageConverter jsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter jsonMessageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter);
        return rabbitTemplate;
    }
}