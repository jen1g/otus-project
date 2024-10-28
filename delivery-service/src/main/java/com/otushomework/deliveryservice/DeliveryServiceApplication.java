package com.otushomework.deliveryservice;

import com.otushomework.deliveryservice.entity.TimeSlot;
import com.otushomework.deliveryservice.repository.TimeSlotRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DeliveryServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(DeliveryServiceApplication.class, args);
    }

    @Bean
    public CommandLineRunner loadTimeSlots(TimeSlotRepository timeSlotRepository) {

        return args -> {
            if (timeSlotRepository.count() == 0) {
                System.out.println("База данных пуста. Заполняем начальными данными...");
                TimeSlot timeSlot1 = new TimeSlot();
                timeSlot1.setTimeslotId("TS1");
                timeSlot1.setTimeslot("08:00-10:00");
                timeSlot1.setMaxValueBooked(20);
                timeSlot1.setBooked(0);

                TimeSlot timeSlot2 = new TimeSlot();
                timeSlot2.setTimeslotId("TS2");
                timeSlot2.setTimeslot("10:00-12:00");
                timeSlot2.setMaxValueBooked(15);
                timeSlot2.setBooked(0);

                TimeSlot timeSlot3 = new TimeSlot();
                timeSlot3.setTimeslotId("TS3");
                timeSlot3.setTimeslot("12:00-14:00");
                timeSlot3.setMaxValueBooked(10);
                timeSlot3.setBooked(0);

                timeSlotRepository.save(timeSlot1);
                timeSlotRepository.save(timeSlot2);
                timeSlotRepository.save(timeSlot3);
            } else {
                System.out.println("База данных уже содержит данные. Пропускаем заполнение.");
            }
        };
    }
}

