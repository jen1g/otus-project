package com.otushomework.orderservice.dto;

import java.util.List;

public class ProductListRequest {

    private List<String> ids;

    // Геттеры и сеттеры

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }
}
