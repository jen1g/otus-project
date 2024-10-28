package com.otushomework.billingservice.controller;

import com.otushomework.billingservice.request.BillingRequest;
import com.otushomework.billingservice.service.BillingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/billing")
public class BillingController {

    private final BillingService billingService;

    public BillingController(BillingService billingService) {
        this.billingService = billingService;
    }

    @PostMapping
    public ResponseEntity<String> createAccount(@RequestParam Long userId) {
        billingService.createAccount(userId);
        return ResponseEntity.ok("Account created");
    }

    @GetMapping("/balance")
    public ResponseEntity<Double> getBalance(@RequestHeader(value = "X-User-Id", required = false) String xUserId) {
        Double balance = billingService.getBalance(Long.valueOf(xUserId));
        return ResponseEntity.ok(balance);
    }

    @PostMapping("/refund")
    public ResponseEntity<String> deposit(@RequestBody BillingRequest billingRequest) {
        System.out.println(billingRequest.getAmount() + " refund to deposit");
        billingService.deposit(billingRequest.getUserId(), billingRequest.getAmount());
        return ResponseEntity.ok("Пользователю возвращены средства " + billingRequest.getAmount());
    }

    @PostMapping("/deposit")
    public ResponseEntity<String> deposit(@RequestHeader(value = "X-User-Id", required = false) String xUserId, @RequestParam double amount) {
        System.out.println(amount + "add to deposit");
        billingService.deposit(Long.valueOf(xUserId), amount);
        return ResponseEntity.ok("Счет пользователя пополнен на " + amount);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<String> withdraw(@RequestBody BillingRequest request) {
        billingService.withdraw(request.getUserId(), request.getAmount());
        return ResponseEntity.ok("Withdrawal successful");
    }
}
