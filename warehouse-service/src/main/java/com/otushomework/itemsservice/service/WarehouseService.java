package com.otushomework.itemsservice.service;

import com.otushomework.itemsservice.dto.WarehouseRequest;
import com.otushomework.itemsservice.entity.Product;
import com.otushomework.itemsservice.entity.ProductItem;
import com.otushomework.itemsservice.exception.InsufficientStockException;
import com.otushomework.itemsservice.repository.WarehouseRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;

    public WarehouseService(WarehouseRepository warehouseRepository) {
        this.warehouseRepository = warehouseRepository;
    }

    @Transactional
    public void reserveProducts(WarehouseRequest warehouseRequest) {
        for (ProductItem item : warehouseRequest.getItems()) {
            Product product = warehouseRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Товар не найден"));
            if (product.getAvailableQuantity() >= item.getQuantity()) {
                product.setAvailableQuantity(product.getAvailableQuantity() - item.getQuantity());
                warehouseRepository.save(product);
            } else {
                throw new InsufficientStockException("Нет достаточного количества на складе ID: " + item.getProductId());
            }
        }
    }

    @Transactional
    public void releaseProducts(WarehouseRequest warehouseRequest) {
        for (ProductItem item : warehouseRequest.getItems()) {
            Product product = warehouseRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Товар не найден"));
            product.setAvailableQuantity(product.getAvailableQuantity() + item.getQuantity());
            warehouseRepository.save(product);
        }
    }

    public List<Product> getProductsByIds(List<String> ids) {
        return warehouseRepository.findAllById(ids);
    }

    public List<Product> getAvailableProducts() {
        return warehouseRepository.findAll();
    }
}
