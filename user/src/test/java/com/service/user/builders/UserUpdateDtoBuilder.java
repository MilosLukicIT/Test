package com.service.user.builders;

import com.service.user.model.dto.UserUpdateDto;

public class UserUpdateDtoBuilder {

    public static UserUpdateDto build(String userId, String firstName, String lastName, String email, String contactInformation){
        return UserUpdateDto.builder()
                .userId(userId)
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .contactInformation(contactInformation)
                .build();
    }
}
