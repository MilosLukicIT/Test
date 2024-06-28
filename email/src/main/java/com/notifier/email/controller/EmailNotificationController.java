package com.notifier.email.controller;


import com.notifier.email.model.EmailNotification;
import com.notifier.email.service.EmailNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.models.UserEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
@RequiredArgsConstructor
public class EmailNotificationController {

    private String groupId;

    private final EmailNotificationService notificationService;

    @KafkaListener(
            topics = "${spring.kafka.topic.name}"
            ,groupId = "${spring.kafka.consumer.group-id}"
    )
    public void receiveRegistrationTopic(@Payload UserEvent userEvent){
        notificationService.sendNotification(userEvent);
    }



    @MessageMapping("/message")
    @SendTo("/specific/reply")
    public String confirmAdmin(@Payload EmailNotification email){

       return notificationService.alertAdmin(email);
    }


}
