package com.otushomework.notificationservice.service;

import com.otushomework.notificationservice.entity.Notification;
import com.otushomework.notificationservice.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository repository;

    @Autowired
    public NotificationService(NotificationRepository repository) {
        this.repository = repository;
    }

    public void sendNotification(Notification notification) {
       Notification n = repository.save(notification);
    }

    public List<Notification> getAllNotifications() {
       return repository.findAll();
    }
}
