package com.otushomework.paymentservice.controller;

import com.otushomework.paymentservice.request.PaymentRequest;
import com.otushomework.paymentservice.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/process")
    public ResponseEntity<String> processPayment(@RequestBody PaymentRequest paymentRequest) {
        paymentService.processPayment(paymentRequest);
        return ResponseEntity.ok("Платеж обработан");
    }

    @PostMapping("/refund")
    public ResponseEntity<?> refundPayment(@RequestBody PaymentRequest paymentRequest) {
        try {
            paymentService.refundPayment(paymentRequest);
            return ResponseEntity.ok("Платеж возвращен");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка возврата: " + e.getMessage());
        }
    }
}
