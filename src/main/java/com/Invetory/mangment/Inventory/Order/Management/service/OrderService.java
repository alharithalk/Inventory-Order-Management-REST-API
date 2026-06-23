package com.Invetory.mangment.Inventory.Order.Management.service;

import com.Invetory.mangment.Inventory.Order.Management.entity.*;
import com.Invetory.mangment.Inventory.Order.Management.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderService(OrderRepository orderRepository,
                        CustomerRepository customerRepository,
                        ProductRepository productRepository,
                        OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.orderItemRepository = orderItemRepository;
    }

    public Order createOrder(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        Order order = new Order();
        order.setCustomer(customer);
        order.setStatus(OrderStatus.DRAFT);
        return orderRepository.save(order);
    }

    public Order addItemToOrder(Long orderId, Long productId, int quantity) {
        Order order = getOrderById(orderId);
        if (order.getStatus() != OrderStatus.DRAFT) {
            throw new RuntimeException("Can only add items to DRAFT orders");
        }
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProduct(product);
        item.setQuantity(quantity);
        item.setUnitPrice(product.getPrice());
        orderItemRepository.save(item);
        return order;
    }

    public Order removeItemFromOrder(Long orderId, Long itemId) {
        Order order = getOrderById(orderId);
        if (order.getStatus() != OrderStatus.DRAFT) {
            throw new RuntimeException("Can only remove items from DRAFT orders");
        }
        OrderItem item = orderItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Order item not found"));
        orderItemRepository.delete(item);
        return order;
    }

    public Order confirmOrder(Long orderId) {
        Order order = getOrderById(orderId);
        if (order.getStatus() != OrderStatus.DRAFT) {
            throw new RuntimeException("Only DRAFT orders can be confirmed");
        }

        // First pass: validate all stock before touching anything
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            if (product.getStockQuantity() < item.getQuantity()) {
                throw new RuntimeException("Insufficient stock for: " + product.getName());
            }
        }

        // Second pass: deduct stock, lock in unit price, accumulate total
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
            productRepository.save(product);
            item.setUnitPrice(product.getPrice());
            orderItemRepository.save(item);
            total = total.add(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        order.setTotalPrice(total);
        order.setStatus(OrderStatus.CONFIRMED);
        return orderRepository.save(order);
    }

    public Order cancelOrder(Long orderId) {
        Order order = getOrderById(orderId);
        if (order.getStatus() == OrderStatus.CONFIRMED) {
            for (OrderItem item : order.getItems()) {
                Product product = item.getProduct();
                product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
                productRepository.save(product);
            }
        } else if (order.getStatus() != OrderStatus.DRAFT) {
            throw new RuntimeException("Cannot cancel order with status: " + order.getStatus());
        }
        order.setStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }

    public Order updateOrderStatus(Long orderId, String status) {
        Order order = getOrderById(orderId);
        OrderStatus current = order.getStatus();
        OrderStatus next = OrderStatus.valueOf(status);
        if (current == OrderStatus.CONFIRMED && next == OrderStatus.SHIPPED) {
            order.setStatus(OrderStatus.SHIPPED);
        } else if (current == OrderStatus.SHIPPED && next == OrderStatus.DELIVERED) {
            order.setStatus(OrderStatus.DELIVERED);
        } else {
            throw new RuntimeException("Invalid status transition: " + current + " -> " + next);
        }
        return orderRepository.save(order);
    }

    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }
}
