package com.service.user.builders;

import com.service.user.model.dto.UserCreateDto;
import com.service.user.model.enums.UserRole;

public class UserCreateDtoBuilder {

    public static UserCreateDto build(String firstName, String lastName, String email, String contactInformation, String password, UserRole userRole){
        return UserCreateDto.builder()
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .userRole(userRole.name())
                .contactInformation(contactInformation)
                .password(password)
                .build();
    }
}
