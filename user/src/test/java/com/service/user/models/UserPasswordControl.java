package com.service.user.models;


import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserPasswordControl {

    private String userId;
    private String email;
    private String password;
}
