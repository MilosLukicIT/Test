package com.service.user.model.dto;

import com.service.user.model.User;
import com.service.user.model.enums.UserRole;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class UserCreateDto {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String contactInformation;
    private String userRole;

}
