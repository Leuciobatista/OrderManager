package com.example.order.service;

import com.example.order.model.Order;
import com.example.order.model.OrderStatus;
import com.example.order.producer.ProcessedOrderProducer;
import com.example.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProcessedOrderProducer processedOrderProducer;

    @Transactional
    public Order createOrder(Order order) {
        if (orderRepository.findById(Long.valueOf(order.getExternalOrderId())).isPresent()) {
            throw new IllegalArgumentException("Pedido com ID externo já existe: " + order.getExternalOrderId());
        }

        order.setCreatedAt(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);

        if (order.getItems() != null) {
            order.getItems().forEach(item -> item.setOrder(order));
        }

        calculateTotalPrice(order);

        Order savedOrder = orderRepository.save(order);
        processedOrderProducer.sendProcessedOrder(savedOrder);

        return savedOrder;
    }

    @Transactional
    public void processOrderFromQueue(Order order) {
        if (orderRepository.findByExternalOrderId(order.getExternalOrderId()).isPresent()) {
            System.out.println("Pedido duplicado ignorado: " + order.getExternalOrderId());
            return;
        }

        Order savedOrder = createOrder(order);
        System.out.println("Pedido processado e salvo: " + savedOrder);
    }

    @Transactional
    public Order updateOrderStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado com ID: " + id));
        order.setStatus(status);
        return orderRepository.save(order);
    }

    @Transactional
    public void deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new IllegalArgumentException("Pedido não encontrado com ID: " + id);
        }
        orderRepository.deleteById(id);
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    private void calculateTotalPrice(Order order) {
        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new IllegalArgumentException("O pedido deve conter pelo menos um item.");
        }

        BigDecimal totalPrice = order.getItems().stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalPrice(totalPrice);
    }
}
