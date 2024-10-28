package com.otushomework.itemsservice.controller;

import com.otushomework.itemsservice.dto.ProductListRequest;
import com.otushomework.itemsservice.service.WarehouseService;
import com.otushomework.itemsservice.dto.WarehouseRequest;
import com.otushomework.itemsservice.entity.Product;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/warehouse")
public class WarehouseController {

    private final WarehouseService warehouseService;

    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @PostMapping("/reserve")
    public ResponseEntity<?> reserveInventory(@RequestBody WarehouseRequest warehouseRequest) {
        try {
            System.out.println(warehouseRequest);
            warehouseService.reserveProducts(warehouseRequest);
            return ResponseEntity.ok("Товар зарезервирован");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Не удалось зарезервировать товар: " + e.getMessage());
        }
    }

    @PostMapping("/release")
    public ResponseEntity<?> releaseInventory(@RequestBody WarehouseRequest warehouseRequest) {
        try {
            warehouseService.releaseProducts(warehouseRequest);
            return ResponseEntity.ok("Резервирование товара снято");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Не удалось разрезервировать товар: " + e.getMessage());
        }
    }

    @PostMapping("/products/list")
    public ResponseEntity<List<Product>> getProductsByIds(@RequestBody ProductListRequest listRequest) {
        List<Product> products = warehouseService.getProductsByIds(listRequest.getIds());
        return ResponseEntity.ok(products);
    }

    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAvailableProducts() {
        List<Product> products = warehouseService.getAvailableProducts();
        return ResponseEntity.ok(products);
    }
}
