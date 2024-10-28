package com.otushomework.orderservice.dto;

import com.otushomework.orderservice.entity.ProductItem;

import java.util.List;

public class OrderRequest {

    private List<ProductItem> items;
    private String timeslotId;

    public List<ProductItem> getItems() {
        return items;
    }

    public void setItems(List<ProductItem> items) {
        this.items = items;
    }

    public String getTimeslotId() {
        return timeslotId;
    }

    public void setTimeslotId(String timeslotId) {
        this.timeslotId = timeslotId;
    }
}
