package com.Invetory.mangment.Inventory.Order.Management.repository;

import com.Invetory.mangment.Inventory.Order.Management.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
