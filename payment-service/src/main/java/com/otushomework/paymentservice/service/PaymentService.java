package com.otushomework.paymentservice.service;

import com.otushomework.paymentservice.entity.PayStatus;
import com.otushomework.paymentservice.entity.Payment;
import com.otushomework.paymentservice.exception.ExternalServiceException;
import com.otushomework.paymentservice.exception.InsufficientFundsException;
import com.otushomework.paymentservice.feign.BillingClient;
import com.otushomework.paymentservice.repository.PaymentRepository;
import com.otushomework.paymentservice.request.BillingRequest;
import com.otushomework.paymentservice.request.PaymentRequest;
import feign.FeignException;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BillingClient billingClient;

    public PaymentService(PaymentRepository paymentRepository, BillingClient billingClient) {
        this.paymentRepository = paymentRepository;
        this.billingClient = billingClient;
    }

    @Transactional
    public void processPayment(PaymentRequest paymentRequest) {
        try {
            BillingRequest billingRequest = new BillingRequest(paymentRequest.getUserId(), paymentRequest.getAmount());
            billingClient.withdraw(billingRequest);

            // Если успешно, сохраняем платеж
            Payment payment = new Payment();
            payment.setOrderId(paymentRequest.getOrderId());
            payment.setUserId(paymentRequest.getUserId());
            payment.setAmount(paymentRequest.getAmount());
            payment.setPayStatus(PayStatus.COMPLETED);
            paymentRepository.save(payment);

        } catch (FeignException feignException) {
            if (feignException.status() == HttpStatus.PAYMENT_REQUIRED.value()) {
                throw new InsufficientFundsException("Недостаточно средств на счете пользователя");
            } else {
                throw new ExternalServiceException("Ошибка при обращении к BillingService", feignException);
            }
        }
    }

    @Transactional
    public void refundPayment(PaymentRequest paymentRequest) {
        BillingRequest billingRequest = new BillingRequest(paymentRequest.getUserId(), paymentRequest.getAmount());
        ResponseEntity<String> response = billingClient.refund(billingRequest);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Ошибка при возврате средств: " + response.getBody());
        }

        Payment payment = paymentRepository.findByOrderId(paymentRequest.getOrderId())
                .orElseThrow(() -> new RuntimeException("Платеж не найден"));
        payment.setPayStatus(PayStatus.REFUNDED);
        paymentRepository.save(payment);
    }
}
