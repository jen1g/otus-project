package com.otushomework.orderservice.controller;

import com.otushomework.orderservice.dto.OrderRequest;
import com.otushomework.orderservice.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<String> createOrder(@RequestBody OrderRequest request, @RequestHeader(value = "X-User-Id", required = false) String xUserId) {
        orderService.createOrder(xUserId, request);
        return ResponseEntity.ok(String.format("Заказ %s принят в обработку", request.getItems()));
    }
}
