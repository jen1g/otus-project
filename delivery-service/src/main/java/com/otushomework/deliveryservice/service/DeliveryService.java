package com.otushomework.deliveryservice.service;

import com.otushomework.deliveryservice.entity.Delivery;
import com.otushomework.deliveryservice.entity.TimeSlot;
import com.otushomework.deliveryservice.repository.DeliveryRepository;
import com.otushomework.deliveryservice.repository.TimeSlotRepository;
import com.otushomework.deliveryservice.request.DeliveryRequest;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final TimeSlotRepository timeSlotRepository;

    public DeliveryService(DeliveryRepository deliveryRepository, TimeSlotRepository timeSlotRepository) {
        this.deliveryRepository = deliveryRepository;
        this.timeSlotRepository = timeSlotRepository;
    }

    @Transactional
    public void scheduleDelivery(DeliveryRequest deliveryRequest) {
        System.out.println(deliveryRequest.getOrderId());
        System.out.println(deliveryRequest.getTimeslotId());
        System.out.println(timeSlotRepository.findAll());
        System.out.println(timeSlotRepository.findById(deliveryRequest.getTimeslotId()).get());
        TimeSlot timeslot = timeSlotRepository.findById(deliveryRequest.getTimeslotId())
                .orElseThrow(() -> new RuntimeException("Не найден необходимый временной интервал"));

        if (timeslot.getBooked() >= timeslot.getMaxValueBooked()) {
            throw new RuntimeException("Доставка в это время недоступна");
        }

        timeslot.setBooked(timeslot.getBooked() + 1);
        timeSlotRepository.save(timeslot);

        Delivery delivery = new Delivery();
        delivery.setOrderId(deliveryRequest.getOrderId());
        delivery.setTimeslot(timeslot);
        deliveryRepository.save(delivery);
    }

    @Transactional
    public void cancelDelivery(String orderId) {
        Delivery delivery = deliveryRepository.findDeliveryByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Доставка не найдена"));

        TimeSlot timeslot = delivery.getTimeslot();
        timeslot.setBooked(timeslot.getBooked() - 1);
        timeSlotRepository.save(timeslot);

        deliveryRepository.delete(delivery);
    }

    public List<TimeSlot> getAvailableTimeslots() {
        List<TimeSlot> timeslots = timeSlotRepository.findAll();
        return timeslots;
    }
}