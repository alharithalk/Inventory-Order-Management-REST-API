package com.Invetory.mangment.Inventory.Order.Management.repository;

import com.Invetory.mangment.Inventory.Order.Management.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
