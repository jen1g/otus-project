package com.otushomework.paymentservice.feign;

import com.otushomework.paymentservice.request.BillingRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "billing-service", url = "${billing.service.url}")
public interface BillingClient {

    @PostMapping("/billing/withdraw")
    ResponseEntity<String> withdraw(@RequestBody BillingRequest request);

    @PostMapping("/billing/refund")
    ResponseEntity<String> refund(@RequestBody BillingRequest request);
}
