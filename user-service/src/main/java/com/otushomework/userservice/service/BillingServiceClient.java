package com.otushomework.userservice.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "billing-service", url = "${billing.service.url}")
public interface BillingServiceClient {

    @PostMapping("/billing")
    String createBillingAccount(@RequestParam("userId") Long userId);
}
