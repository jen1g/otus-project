package com.otushomework.billingservice.request;

public class BillingRequest {

    private Long userId;
    private Double amount;

    public BillingRequest() {
    }

    public BillingRequest(Long userId, Double amount) {
        this.userId = userId;
        this.amount = amount;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
