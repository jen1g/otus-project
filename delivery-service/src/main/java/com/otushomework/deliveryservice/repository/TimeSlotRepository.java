package com.otushomework.deliveryservice.repository;

import com.otushomework.deliveryservice.entity.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimeSlotRepository extends JpaRepository<TimeSlot, String> {
}