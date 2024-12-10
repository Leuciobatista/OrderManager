package com.example.order.consumer;

import com.example.order.model.Order;
import com.example.order.producer.ProcessedOrderProducer;
import com.example.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IncomingOrderConsumer {

    private final OrderService orderService;
    private final ProcessedOrderProducer processedOrderProducer;

    @RabbitListener(queues = "incoming-orders")
    public void consumeIncomingOrder(Order order) {
        System.out.println("Consumindo pedido da fila: " + order);

        Order processedOrder = orderService.createOrder(order);

        processedOrderProducer.sendProcessedOrder(processedOrder);

        System.out.println("Pedido processado e enviado para Produto B: " + processedOrder);
    }
}
