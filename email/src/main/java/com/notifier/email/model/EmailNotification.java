package com.notifier.email.model;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Date;


@Document(collection = "emails")
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailNotification {

    @MongoId
    private String confirmationId;

    private String adminId;
    private Date timeRegistered;
    private String message;
    private String userId;
}
