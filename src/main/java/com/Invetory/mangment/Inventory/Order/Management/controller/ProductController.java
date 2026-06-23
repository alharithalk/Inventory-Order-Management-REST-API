package com.Invetory.mangment.Inventory.Order.Management.controller;

import com.Invetory.mangment.Inventory.Order.Management.entity.Product;
import com.Invetory.mangment.Inventory.Order.Management.service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Data
    static class CreateProductRequest {
        @NotBlank
        private String name;
        @NotNull
        private BigDecimal price;
        @NotNull
        private Integer stockQuantity;
        @NotNull
        private Long categoryId;
    }

    @Data
    static class StockAdjustmentRequest {
        private int amount;
    }

    @PostMapping
    public ResponseEntity<?> createProduct(@Valid @RequestBody CreateProductRequest request) {
        try {
            Product product = productService.createProduct(
                    request.getName(),
                    request.getPrice(),
                    request.getStockQuantity(),
                    request.getCategoryId()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(product);
        } catch (RuntimeException e) {
            return handleException(e);
        }
    }

    @PostMapping("/{productId}/stock-adjustment")
    public ResponseEntity<?> adjustStock(@PathVariable Long productId,
                                         @Valid @RequestBody StockAdjustmentRequest request) {
        try {
            Product product = productService.adjustStock(productId, request.getAmount());
            return ResponseEntity.ok(product);
        } catch (RuntimeException e) {
            return handleException(e);
        }
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<Product>> getLowStock(@RequestParam int threshold) {
        return ResponseEntity.ok(productService.getLowStockProducts(threshold));
    }

    private ResponseEntity<String> handleException(RuntimeException e) {
        String message = e.getMessage() != null ? e.getMessage() : "An error occurred";
        if (message.toLowerCase().contains("not found")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body(message);
    }
}
