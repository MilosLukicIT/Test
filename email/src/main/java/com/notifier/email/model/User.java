package com.notifier.email.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class User {

    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String contactInformation;
    private String confirmationToken;
}
