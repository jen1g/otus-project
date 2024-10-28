package com.otushomework.itemsservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class WarehouseExceptionHandler {

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<String> handleInsufficientFundsException(InsufficientStockException ex) {
        return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(ex.getMessage());
    }
}
