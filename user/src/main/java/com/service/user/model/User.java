package com.service.user.model;

import com.service.user.model.enums.UserRole;
import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Document(collection = "users")
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class User implements UserDetails {

    @MongoId
    private String userId;

    @Field(value = "first_name")
    private String firstName;
    @Field(value = "last_name")
    private String lastName;

    @Indexed(unique = true)
    private String email;
    private String password;
    @Field(value = "contact_information")
    private String contactInformation;

    private boolean confirmedEmail = false;

    private String confirmationToken;
    private Date confirmationTimeStamp = new Date();

    private UserRole userRole;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return this.email;
    }
}
