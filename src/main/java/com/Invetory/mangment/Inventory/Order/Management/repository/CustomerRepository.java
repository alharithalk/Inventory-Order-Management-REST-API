package com.Invetory.mangment.Inventory.Order.Management.repository;

import com.Invetory.mangment.Inventory.Order.Management.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
