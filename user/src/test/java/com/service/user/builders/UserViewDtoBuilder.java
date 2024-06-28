package com.service.user.builders;

import com.service.user.model.User;
import com.service.user.model.dto.UserCreateDto;
import com.service.user.model.dto.UserViewDto;
import com.service.user.model.enums.UserRole;


public class UserViewDtoBuilder {

    public static UserViewDto build(User user){
        return UserViewDto.builder()
                .userId(user.getUserId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .userRole(user.getUserRole().name())
                .contactInformation(user.getContactInformation())
                .build();
    }

    public static UserViewDto build(String userId, String firstName, String lastName, String email, String contactInformation, UserRole userRole){
        return UserViewDto.builder()
                .userId(userId)
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .userRole(userRole.name())
                .contactInformation(contactInformation)
                .build();
    }
}
