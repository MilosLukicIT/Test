package com.service.user.builders;

import com.service.user.model.User;
import com.service.user.models.UserPasswordControl;

public class UserPasswordControlBuilder {

    public static UserPasswordControl build(User user){

        return UserPasswordControl.builder()
                .userId(user.getUserId())
                .password(user.getPassword())
                .email(user.getEmail())
                .build();
    }
}
