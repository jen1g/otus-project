package com.otushomework.orderservice.feign;

import com.otushomework.orderservice.dto.PaymentRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service", url = "${payment.service.url}") // todo
public interface PaymentClient {

    @PostMapping("/payments/process")
    ResponseEntity<String> processPayment(@RequestBody PaymentRequest request);

    @PostMapping("/payments/refund")
    ResponseEntity<String> refundPayment(@RequestBody PaymentRequest request);
}