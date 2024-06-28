package com.service.user.builders;

import com.service.user.model.User;
import com.service.user.model.enums.UserRole;

import java.util.Date;

public class UserBuilder {

    public static User build(String userId, String firstName, String lastName, String email,
                             String contactInformation, String password,
                             UserRole userRole, Boolean confirmedEmail, String confirmationToken, Date confirmationTimeStamp){
        return User.builder()
                .userId(userId)
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .userRole(userRole)
                .contactInformation(contactInformation)
                .password(password)
                .confirmedEmail(confirmedEmail)
                .confirmationToken(confirmationToken)
                .confirmationTimeStamp(confirmationTimeStamp)
                .build();
    }

    public static User build(String firstName, String lastName, String email,
                             String contactInformation, String password,
                             UserRole userRole, Boolean confirmedEmail, String confirmationToken, Date confirmationTimeStamp){
        return User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .userRole(userRole)
                .contactInformation(contactInformation)
                .password(password)
                .confirmedEmail(confirmedEmail)
                .confirmationToken(confirmationToken)
                .confirmationTimeStamp(confirmationTimeStamp)
                .build();
    }
}
