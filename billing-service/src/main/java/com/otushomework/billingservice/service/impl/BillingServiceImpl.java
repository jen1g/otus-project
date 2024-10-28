package com.otushomework.billingservice.service.impl;

import com.otushomework.billingservice.exception.InsufficientFundsException;
import com.otushomework.billingservice.model.Account;
import com.otushomework.billingservice.repository.AccountRepository;
import com.otushomework.billingservice.service.BillingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BillingServiceImpl implements BillingService {

    private final AccountRepository accountRepository;

    public BillingServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Account createAccount(Long userId) {
        Account account = new Account(userId, 0.0);
        return accountRepository.save(account);
    }

    @Override
    public Double getBalance(Long userId) {
        Account account = accountRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        return account.getBalance();
    }

    @Override
    @Transactional
    public void deposit(Long userId, Double amount) {
        Account account = accountRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        account.setBalance(account.getBalance() + amount);
        accountRepository.save(account);
    }

    @Override
    @Transactional
    public void withdraw(Long userId, Double amount) {
        Account account = accountRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Аккаунт не найден"));
        if (account.getBalance() >= amount) {
            account.setBalance(account.getBalance() - amount);
            accountRepository.save(account);
        } else {
            throw new InsufficientFundsException("Недостаточно средств на счете пользователя");
        }
    }
}
