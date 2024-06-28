package com.service.user.model;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class EmailNotificationConfirmRegistration {

    private String adminId;
    private Date timeRegistered;
    private String message;
    private String userId;
}
