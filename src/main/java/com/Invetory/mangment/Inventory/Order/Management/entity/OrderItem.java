package com.Invetory.mangment.Inventory.Order.Management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OrdItem_id")
    private Long ordItemId;

    @ManyToOne
    @JoinColumn(name = "Ord_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "Prod_id")
    private Product product;

    @NotNull
    @Min(1)
    private Integer quantity;

    @NotNull
    @Column(name = "unit_price")
    private BigDecimal unitPrice;
}
