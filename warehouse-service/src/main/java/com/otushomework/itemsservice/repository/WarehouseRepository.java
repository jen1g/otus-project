package com.otushomework.itemsservice.repository;

import com.otushomework.itemsservice.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarehouseRepository extends JpaRepository<Product, String> {
}
