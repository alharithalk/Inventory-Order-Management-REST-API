package com.Invetory.mangment.Inventory.Order.Management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Cat_id")
    private Long catId;

    @NotBlank
    private String name;

    @OneToMany(mappedBy = "category")
    private List<Product> products;
}
