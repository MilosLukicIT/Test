package com.service.user.mapper;


import com.service.user.builders.UserBuilder;
import com.service.user.builders.UserCreateDtoBuilder;
import com.service.user.builders.UserUpdateDtoBuilder;
import com.service.user.model.User;
import com.service.user.model.dto.UserCreateDto;
import com.service.user.model.dto.UserUpdateDto;
import com.service.user.model.dto.UserViewDto;
import com.service.user.model.enums.UserRole;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@SpringBootTest
public class UserMapperTest {


    @Autowired
    private UserMapper userMapper;

    private UserCreateDto userCreateDto;
    private List<User> users;
    private User user;
    private UserUpdateDto userUpdateDto;


    @BeforeEach
    void init(){
        user = UserBuilder.build("Milos", "Lukic", "milos" + UUID.randomUUID() + "@mail.com", "064123123",
                "lozinka", UserRole.ADMIN, true, "093482094820984",
                new Date());

        userCreateDto = UserCreateDtoBuilder.build("Milos", "Lukic", "milo.test.ns@gmail.com",
                "064123123", "lozinka", UserRole.ADMIN);

        userUpdateDto = UserUpdateDtoBuilder.build("12e12e32e23e2q3", "Mis", "Lic",
                "milo.test.ns@mail.com", "0632323232");

        users = new ArrayList<>();

        users.add(user);
        users.add(user);
        users.add(user);
    }

    @Test
    public void userToViewDto(){

        UserViewDto userAfterMap = userMapper.toViewDtoFromUser(user);

        Assertions.assertThat(userAfterMap.getUserId()).isEqualTo(user.getUserId());
        Assertions.assertThat(userAfterMap.getFirstName()).isEqualTo(user.getFirstName());
        Assertions.assertThat(userAfterMap.getLastName()).isEqualTo(user.getLastName());
        Assertions.assertThat(userAfterMap.getEmail()).isEqualTo(user.getEmail());
        Assertions.assertThat(userAfterMap.getContactInformation()).isEqualTo(user.getContactInformation());
        Assertions.assertThat(userAfterMap.getUserRole()).isEqualTo(user.getUserRole().name());

    }

    @Test
    public void userToViewDtoNull(){

        UserViewDto userAfterMap = userMapper.toViewDtoFromUser(null);
        Assertions.assertThat(userAfterMap).isNull();
    }

    @Test
    public void createDtoToUser(){

        User userAfterMap = userMapper.toUserFromCreate(userCreateDto);

        Assertions.assertThat(userAfterMap.getFirstName()).isEqualTo(userCreateDto.getFirstName());
        Assertions.assertThat(userAfterMap.getLastName()).isEqualTo(userCreateDto.getLastName());
        Assertions.assertThat(userAfterMap.getEmail()).isEqualTo(userCreateDto.getEmail());
        Assertions.assertThat(userAfterMap.getContactInformation()).isEqualTo(userCreateDto.getContactInformation());
        Assertions.assertThat(userAfterMap.getUserRole().name()).isEqualTo(userCreateDto.getUserRole());
        Assertions.assertThat(userAfterMap.getPassword()).isEqualTo(userCreateDto.getPassword());

    }

    @Test
    public void createDtoToUserNull(){

        User userAfterMap = userMapper.toUserFromCreate(null);
        Assertions.assertThat(userAfterMap).isNull();

    }

    @Test
    public void userToListViewDto(){

        List<UserViewDto> userAfterMap = userMapper.toViewList(users);

        Assertions.assertThat(userAfterMap.size()).isEqualTo(users.size());

        for(int i = 0; i< userAfterMap.size(); i++){
            Assertions.assertThat(userAfterMap.get(i).getUserId()).isEqualTo(users.get(i).getUserId());
            Assertions.assertThat(userAfterMap.get(i).getFirstName()).isEqualTo(users.get(i).getFirstName());
            Assertions.assertThat(userAfterMap.get(i).getLastName()).isEqualTo(users.get(i).getLastName());
            Assertions.assertThat(userAfterMap.get(i).getEmail()).isEqualTo(users.get(i).getEmail());
            Assertions.assertThat(userAfterMap.get(i).getContactInformation()).isEqualTo(users.get(i).getContactInformation());
            Assertions.assertThat(userAfterMap.get(i).getUserRole()).isEqualTo(users.get(i).getUserRole().name());
        }
    }

    @Test
    public void userToListViewDtoNull(){

        List<UserViewDto> userAfterMap = userMapper.toViewList(null);

        Assertions.assertThat(userAfterMap).isNull();
    }

    @Test
    public void userUpdateToUser(){
        User userAfterMap = new User();
        userUpdateDto.toUser(userAfterMap);

        Assertions.assertThat(userAfterMap.getFirstName()).isEqualTo(userUpdateDto.getFirstName());
        Assertions.assertThat(userAfterMap.getLastName()).isEqualTo(userUpdateDto.getLastName());
        Assertions.assertThat(userAfterMap.getEmail()).isEqualTo(userUpdateDto.getEmail());
        Assertions.assertThat(userAfterMap.getContactInformation()).isEqualTo(userUpdateDto.getContactInformation());
    }

}
