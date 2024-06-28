package com.service.user.model.dto;

import com.service.user.model.User;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class UserUpdateDto {

    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String contactInformation;


    public void toUser(User user){
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setContactInformation(contactInformation);
    }
}
