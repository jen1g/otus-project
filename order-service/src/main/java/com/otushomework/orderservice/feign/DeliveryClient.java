package com.otushomework.orderservice.feign;

import com.otushomework.orderservice.dto.DeliveryRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "delivery-service", url = "${delivery.service.url}")
public interface DeliveryClient {

    @PostMapping("/delivery/schedule")
    ResponseEntity<String> scheduleDelivery(@RequestBody DeliveryRequest request);

    @PostMapping("/delivery/cancel")
    ResponseEntity<String> cancelDelivery(@RequestParam("orderId") String orderId);
}