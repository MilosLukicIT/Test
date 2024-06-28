package com.notifier.email.service;

import com.notifier.email.exception.FailedExecution;
import com.notifier.email.exception.NotificationNotFound;
import com.notifier.email.model.EmailNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.models.UserEvent;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationService {

    private final JavaMailSender emailSender;
    private final MongoTemplate mongoTemplate;


    public void sendNotification(UserEvent userEvent) {
        try {

            String text = "Hello " + userEvent.getFirstName() + " " + userEvent.getLastName() + ", "
                    +"\n\nThank you for creating an account with us!"
                    + "\nPlease confirm your registration: http://localhost:8080/api/v1/user/confirm_registration/"
                    + userEvent.getConfirmationToken();
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("ml.test.app.spring@gmail.com");
            message.setTo("milo.test.ns@gmail.com");
            message.setSubject("Registration confirmation for user account");
            message.setText(text);
            emailSender.send(message);
            log.info("Registration confirmation link sent successfully");
        } catch (MailException e) {
            throw new RuntimeException(e);
        }
    }

    public String alertAdmin(EmailNotification emailNotification){

        mongoTemplate.save(emailNotification);

        log.info("Alert admin");

        return "Alerted admins about registration";
    }


    public EmailNotification getOneNotification(String userId){

        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId));

        if(!mongoTemplate.exists(query, EmailNotification.class)){
            throw new NotificationNotFound("Not found");
        }

        return mongoTemplate.findOne(query,EmailNotification.class);
    }

}
