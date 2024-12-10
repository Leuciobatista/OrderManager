package com.example.order.producer;

import com.example.order.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProcessedOrderProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendProcessedOrder(Order order) {
        rabbitTemplate.convertAndSend("orders-exchange", "orders.processed", order);
        System.out.println("Pedido enviado para Produto B: " + order);
    }
}

