package com.otushomework.notificationservice.controller;

import com.otushomework.notificationservice.entity.Notification;
import com.otushomework.notificationservice.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private NotificationService service;

    @Autowired
    public NotificationController(NotificationService service) {
        this.service = service;
    }


    @GetMapping
    public List<Notification> getMessage() {
        List<Notification> notifications = service.getAllNotifications();
        return notifications;
    }

//    @GetMapping
//    public List<Notification> getMessage(@RequestHeader(value = "X-User-Id", required = false) String xUserId) {
//        Long userId = Long.parseLong(xUserId);
//        List<Notification> notifications = service.getAllNotifications(userId);
//        return notifications;
//    }
}
