package com.Invetory.mangment.Inventory.Order.Management.repository;

import com.Invetory.mangment.Inventory.Order.Management.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
