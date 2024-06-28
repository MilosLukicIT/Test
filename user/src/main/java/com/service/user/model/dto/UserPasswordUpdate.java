package com.service.user.model.dto;


import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class UserPasswordUpdate {

    String userId;
    String oldPassword;
    String newPassword;
}
