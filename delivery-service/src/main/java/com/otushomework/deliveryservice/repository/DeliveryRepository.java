package com.otushomework.deliveryservice.repository;

import com.otushomework.deliveryservice.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeliveryRepository extends JpaRepository<Delivery, Long>  {

    Optional<Delivery> findDeliveryByOrderId(String orderId);
}
