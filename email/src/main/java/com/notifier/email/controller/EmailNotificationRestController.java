package com.notifier.email.controller;


import com.notifier.email.model.EmailNotification;
import com.notifier.email.service.EmailNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class EmailNotificationRestController {


    private final EmailNotificationService notificationService;

    @GetMapping("/notifications/{userId}")
    public ResponseEntity<EmailNotification> getAllNotifications(@PathVariable String userId){
        return ResponseEntity.ok(notificationService.getOneNotification(userId));
    }

}
