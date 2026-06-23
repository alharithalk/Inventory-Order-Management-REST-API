package com.Invetory.mangment.Inventory.Order.Management.controller;

import com.Invetory.mangment.Inventory.Order.Management.entity.Order;
import com.Invetory.mangment.Inventory.Order.Management.service.OrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Data
    static class AddItemRequest {
        @NotNull
        private Long productId;
        @Min(1)
        private int quantity;
    }

    @Data
    static class StatusRequest {
        @NotBlank
        private String status;
    }

    @PostMapping("/{orderId}/items")
    public ResponseEntity<Order> addItem(@PathVariable Long orderId,
                                         @Valid @RequestBody AddItemRequest request) {
        Order order = orderService.addItemToOrder(orderId, request.getProductId(), request.getQuantity());
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @DeleteMapping("/{orderId}/items/{itemId}")
    public ResponseEntity<Void> removeItem(@PathVariable Long orderId, @PathVariable Long itemId) {
        orderService.removeItemFromOrder(orderId, itemId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{orderId}/confirm")
    public ResponseEntity<Order> confirmOrder(@PathVariable Long orderId) {
        Order order = orderService.confirmOrder(orderId);
        return ResponseEntity.ok(order);
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<Order> cancelOrder(@PathVariable Long orderId) {
        Order order = orderService.cancelOrder(orderId);
        return ResponseEntity.ok(order);
    }

    @PostMapping("/{orderId}/status")
    public ResponseEntity<Order> updateStatus(@PathVariable Long orderId,
                                              @Valid @RequestBody StatusRequest request) {
        Order order = orderService.updateOrderStatus(orderId, request.getStatus());
        return ResponseEntity.ok(order);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrder(@PathVariable Long orderId) {
        Order order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(order);
    }
}
