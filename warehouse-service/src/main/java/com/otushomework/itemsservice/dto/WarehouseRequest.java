package com.otushomework.itemsservice.dto;

import com.otushomework.itemsservice.entity.ProductItem;

import java.util.List;

public class WarehouseRequest {

    private String orderId;
    private List<ProductItem> items;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public List<ProductItem> getItems() {
        return items;
    }

    public void setItems(List<ProductItem> items) {
        this.items = items;
    }
}
