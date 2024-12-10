package com.example.order.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String INCOMING_ORDERS_QUEUE = "incoming-orders";
    public static final String PROCESSED_ORDERS_QUEUE = "processed-orders";

    public static final String EXCHANGE = "orders-exchange";

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter converter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter);
        return rabbitTemplate;
    }

    @Bean
    public Queue incomingOrdersQueue() {
        return new Queue(INCOMING_ORDERS_QUEUE, true);
    }

    @Bean
    public Queue processedOrdersQueue() {
        return new Queue(PROCESSED_ORDERS_QUEUE, true);
    }

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Binding bindIncomingOrdersQueue(Queue incomingOrdersQueue, TopicExchange exchange) {
        return BindingBuilder.bind(incomingOrdersQueue).to(exchange).with("orders.incoming");
    }

    @Bean
    public Binding bindProcessedOrdersQueue(Queue processedOrdersQueue, TopicExchange exchange) {
        return BindingBuilder.bind(processedOrdersQueue).to(exchange).with("orders.processed");
    }
}
