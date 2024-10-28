package com.otushomework.orderservice.service.impl;

import com.otushomework.orderservice.dto.*;
import com.otushomework.orderservice.entity.Order;
import com.otushomework.orderservice.entity.OrderStatus;
import com.otushomework.orderservice.entity.ProductItem;
import com.otushomework.orderservice.exception.ExternalServiceException;
import com.otushomework.orderservice.exception.InsufficientFundsException;
import com.otushomework.orderservice.exception.InsufficientStockException;
import com.otushomework.orderservice.feign.DeliveryClient;
import com.otushomework.orderservice.feign.PaymentClient;
import com.otushomework.orderservice.feign.WarehouseClient;
import com.otushomework.orderservice.repository.OrderRepository;
import com.otushomework.orderservice.service.KafkaMessageProducer;
import com.otushomework.orderservice.service.OrderService;
import feign.FeignException;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final PaymentClient paymentClient;
    private final WarehouseClient warehouseClient;
    private final DeliveryClient deliveryClient;
    private final OrderRepository orderRepository;
    private final KafkaMessageProducer kafkaProducer;

    public OrderServiceImpl(PaymentClient paymentClient, WarehouseClient warehouseClient, DeliveryClient deliveryClient,
                            OrderRepository orderRepository, KafkaMessageProducer kafkaProducer) {
        this.paymentClient = paymentClient;
        this.warehouseClient = warehouseClient;
        this.deliveryClient = deliveryClient;
        this.orderRepository = orderRepository;
        this.kafkaProducer = kafkaProducer;
    }

    @Transactional
    public void createOrder(String userId, OrderRequest orderRequest) {
        long longUserId = Long.parseLong(userId);
        Order order = new Order();
        order.setOrderId(UUID.randomUUID().toString());
        order.setUserId(longUserId);
        order.setStatus(OrderStatus.PENDING);

        String orderId = order.getOrderId();
        double totalAmount = 0.0;

        // Флаги состояния операций
        boolean paymentProcessed = false;
        boolean productsReserved = false;
        boolean deliveryScheduled = false;

        try {
            // Шаг 1: Получение цен продуктов и расчет суммы
            totalAmount = calculateTotalAmount(orderRequest);

            // Устанавливаем totalAmount в заказ
            order.setTotalAmount(totalAmount);
            orderRepository.save(order);

            // Шаг 2: Обработка платежа
            PaymentRequest paymentRequest = new PaymentRequest();
            paymentRequest.setOrderId(orderId);
            paymentRequest.setUserId(longUserId);
            paymentRequest.setAmount(totalAmount);

            paymentClient.processPayment(paymentRequest);
            paymentProcessed = true;

            // Шаг 3: Резервирование товаров
            WarehouseRequest warehouseRequest = new WarehouseRequest();
            warehouseRequest.setOrderId(orderId);
            warehouseRequest.setItems(orderRequest.getItems());
            warehouseClient.reserveProducts(warehouseRequest);
            productsReserved = true;

            // Шаг 4: Планирование доставки
            DeliveryRequest deliveryRequest = new DeliveryRequest();
            deliveryRequest.setOrderId(orderId);
            deliveryRequest.setTimeslotId(orderRequest.getTimeslotId());
            deliveryClient.scheduleDelivery(deliveryRequest);
            deliveryScheduled = true;

            // Шаг 5: Обновляем статус заказа на "COMPLETED"
            order.setStatus(OrderStatus.COMPLETED);
            orderRepository.save(order);

            // Шаг 6: Отправляем уведомление
            kafkaProducer.sendMessage("order-notifications", "Заказ успешно обработан. Номер заказа: " + order.getOrderId());

        } catch (Exception e) {
            handleCompensation(orderId, orderRequest, longUserId, totalAmount,
                    paymentProcessed, productsReserved, deliveryScheduled, e);
            if (e instanceof FeignException feignException) {
                if (feignException.status() == HttpStatus.PAYMENT_REQUIRED.value()) {
                    throw new InsufficientFundsException("Недостаточно средств на счете пользователя");
                } else if (feignException.status() == HttpStatus.CONFLICT.value()) {
                    throw new InsufficientStockException("Недостаточно товара на складе");
                } else {
                    throw new ExternalServiceException("Ошибка при обращении к внешнему сервису", feignException);
                }
            } else {
                throw new RuntimeException("Обработка заказа не удалась: " + e.getMessage(), e);
            }
        }
    }

    private void handleCompensation(String orderId, OrderRequest orderRequest, long longUserId, double totalAmount,
                                    boolean paymentProcessed, boolean productsReserved, boolean deliveryScheduled,
                                    Exception originalException) {
        // Откат в обратном порядке
        System.out.println("rollback operations");
        try {
            if (deliveryScheduled) {
                try {
                    System.out.println("rollback delivery");
                    deliveryClient.cancelDelivery(orderId);
                } catch (Exception ex) {
                    System.err.println("rollback delivery error");
                }
            }
            if (productsReserved) {
                try {
                    System.out.println("rollback reservation");
                    WarehouseRequest warehouseRequest = new WarehouseRequest();
                    warehouseRequest.setOrderId(orderId);
                    warehouseRequest.setItems(orderRequest.getItems());
                    warehouseClient.dereservationProducts(warehouseRequest);
                } catch (Exception ex) {
                    System.err.println("rollback reservation products error");
                }
            }
            if (paymentProcessed) {
                try {
                    System.out.println("rollback payment");
                    PaymentRequest paymentRequest = new PaymentRequest();
                    paymentRequest.setOrderId(orderId);
                    paymentRequest.setUserId(longUserId);
                    paymentRequest.setAmount(totalAmount);
                    paymentClient.refundPayment(paymentRequest);
                } catch (Exception ex) {
                    System.err.println("rollback payment error");
                }
            }
            // Обновляем статус заказа на "FAILED"
            Order order = orderRepository.findById(orderId).orElse(null);
            if (order != null) {
                order.setStatus(OrderStatus.FAILED);
                orderRepository.save(order);
            }
        } catch (Exception ex) {
            System.err.println("rollback error");
            kafkaProducer.sendMessage("order-notifications", "Заказ отменен.");
        }
    }

    private double calculateTotalAmount(OrderRequest orderRequest) {
        List<String> productIds = orderRequest.getItems().stream()
                .map(ProductItem::getProductId)
                .collect(Collectors.toList());

        ProductListRequest idsRequest = new ProductListRequest();
        idsRequest.setIds(productIds);

        ResponseEntity<List<ProductRequest>> productResponse = warehouseClient.getProductsByIds(idsRequest);
        List<ProductRequest> productRequests = productResponse.getBody();

        if (productRequests == null || productRequests.isEmpty()) {
            throw new RuntimeException("Не удалось получить информацию о продуктах");
        }

        Map<String, Double> productPriceMap = productRequests.stream()
                .collect(Collectors.toMap(ProductRequest::getId, ProductRequest::getPrice));

        double totalAmount = 0.0;

        for (ProductItem item : orderRequest.getItems()) {
            Double price = productPriceMap.get(item.getProductId());
            if (price == null) {
                throw new RuntimeException("Следующие продукты не найдены: " + item.getProductId());
            }
            totalAmount += price * item.getQuantity();
        }
        return totalAmount;
    }
}
