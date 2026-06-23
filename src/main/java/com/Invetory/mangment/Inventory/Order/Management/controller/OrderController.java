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
    public ResponseEntity<?> addItem(@PathVariable Long orderId,
                                     @Valid @RequestBody AddItemRequest request) {
        try {
            Order order = orderService.addItemToOrder(orderId, request.getProductId(), request.getQuantity());
            return ResponseEntity.status(HttpStatus.CREATED).body(order);
        } catch (RuntimeException e) {
            return handleException(e);
        }
    }

    @DeleteMapping("/{orderId}/items/{itemId}")
    public ResponseEntity<?> removeItem(@PathVariable Long orderId, @PathVariable Long itemId) {
        try {
            orderService.removeItemFromOrder(orderId, itemId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return handleException(e);
        }
    }

    @PostMapping("/{orderId}/confirm")
    public ResponseEntity<?> confirmOrder(@PathVariable Long orderId) {
        try {
            Order order = orderService.confirmOrder(orderId);
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return handleException(e);
        }
    }

    @PostMapping("/{orderId}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long orderId,
                                          @Valid @RequestBody StatusRequest request) {
        try {
            Order order = orderService.updateOrderStatus(orderId, request.getStatus());
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return handleException(e);
        }
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrder(@PathVariable Long orderId) {
        try {
            Order order = orderService.getOrderById(orderId);
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return handleException(e);
        }
    }

    private ResponseEntity<String> handleException(RuntimeException e) {
        String message = e.getMessage() != null ? e.getMessage() : "An error occurred";
        if (message.toLowerCase().contains("not found")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body(message);
    }
}
