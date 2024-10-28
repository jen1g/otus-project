package com.otushomework.orderservice.service;

import com.otushomework.orderservice.dto.OrderRequest;
import com.otushomework.orderservice.exception.InsufficientFundsException;

public interface OrderService {

    void createOrder(String userId, OrderRequest orderRequest) throws InsufficientFundsException;
}
