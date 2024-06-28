package com.service.user.model.dto;

import com.service.user.model.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserViewDto {
    private String userId;

    private String firstName;
    private String lastName;
    private String email;
    private String contactInformation;
    private String userRole;

}
