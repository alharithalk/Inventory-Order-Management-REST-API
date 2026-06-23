package com.Invetory.mangment.Inventory.Order.Management.controller;

import com.Invetory.mangment.Inventory.Order.Management.entity.Customer;
import com.Invetory.mangment.Inventory.Order.Management.entity.Order;
import com.Invetory.mangment.Inventory.Order.Management.service.CustomerService;
import com.Invetory.mangment.Inventory.Order.Management.service.OrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;
    private final OrderService orderService;

    public CustomerController(CustomerService customerService, OrderService orderService) {
        this.customerService = customerService;
        this.orderService = orderService;
    }

    @Data
    static class CustomerRequest {
        @NotBlank
        private String name;
        @NotBlank
        @Email
        private String email;
    }

    @PostMapping
    public ResponseEntity<Customer> createCustomer(@Valid @RequestBody CustomerRequest request) {
        Customer customer = customerService.createCustomer(request.getName(), request.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(customer);
    }

    @PostMapping("/{customerId}/orders")
    public ResponseEntity<Order> createOrder(@PathVariable Long customerId) {
        Order order = orderService.createOrder(customerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }
}
