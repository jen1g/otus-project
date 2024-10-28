package com.otushomework.notificationservice.service.kafka;

import com.otushomework.notificationservice.entity.Notification;
import com.otushomework.notificationservice.service.NotificationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaMessageConsumer {

    private final NotificationService service;

    public KafkaMessageConsumer(NotificationService service) {
        this.service = service;
    }

    @KafkaListener(topics = "order-notifications", groupId = "notification-group-1")
    public void listen(String message) {
        Notification notification = new Notification();
        notification.setMessage(message);
        System.out.println(message);
        service.sendNotification(notification);
    }
}
