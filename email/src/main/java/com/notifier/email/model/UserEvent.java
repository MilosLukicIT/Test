package com.notifier.email.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserEvent {

    private String message;
    private String status;
    private User user;

}
