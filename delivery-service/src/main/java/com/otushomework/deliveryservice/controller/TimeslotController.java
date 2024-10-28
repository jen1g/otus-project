package com.otushomework.deliveryservice.controller;

import com.otushomework.deliveryservice.entity.TimeSlot;
import com.otushomework.deliveryservice.repository.TimeSlotRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/timeslots")
public class TimeslotController {

    private final TimeSlotRepository timeslotRepository;

    public TimeslotController(TimeSlotRepository timeslotRepository) {
        this.timeslotRepository = timeslotRepository;
    }

    @GetMapping("/available")
    public List<TimeSlot> getAvailableTimeslots() {
        return timeslotRepository.findAll().stream()
                .filter(timeslot -> timeslot.getBooked() < timeslot.getMaxValueBooked())
                .collect(Collectors.toList());
    }
}
