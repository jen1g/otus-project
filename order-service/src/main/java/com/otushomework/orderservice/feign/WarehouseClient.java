package com.otushomework.orderservice.feign;

import com.otushomework.orderservice.dto.ProductListRequest;
import com.otushomework.orderservice.dto.ProductRequest;
import com.otushomework.orderservice.dto.WarehouseRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "warehouse-service", url = "${warehouse.service.url}")
public interface WarehouseClient {

    @PostMapping("/warehouse/reserve")
    ResponseEntity<String> reserveProducts(@RequestBody WarehouseRequest request);

    @PostMapping("/warehouse/release")
    ResponseEntity<String> dereservationProducts(@RequestBody WarehouseRequest request);

    @PostMapping("/warehouse/products/list")
    ResponseEntity<List<ProductRequest>> getProductsByIds(@RequestBody ProductListRequest request);
}
