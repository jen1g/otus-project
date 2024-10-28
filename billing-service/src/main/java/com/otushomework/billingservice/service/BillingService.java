package com.otushomework.billingservice.service;

import com.otushomework.billingservice.model.Account;
import org.springframework.stereotype.Service;

@Service
public interface BillingService {

    Account createAccount(Long userId);
    Double getBalance(Long userId);
    void deposit(Long userId, Double amount);
    void withdraw(Long userId, Double amount);
}
