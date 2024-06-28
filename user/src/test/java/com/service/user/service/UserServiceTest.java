package com.service.user.service;


import com.service.user.builders.UserBuilder;
import com.service.user.builders.UserCreateDtoBuilder;
import com.service.user.builders.UserUpdateDtoBuilder;
import com.service.user.builders.UserViewDtoBuilder;
import com.service.user.exceptions.UserNotFoundException;
import com.service.user.mapper.UserMapper;
import com.service.user.model.User;
import com.service.user.model.dto.UserCreateDto;
import com.service.user.model.dto.UserPasswordUpdate;
import com.service.user.model.dto.UserUpdateDto;
import com.service.user.model.dto.UserViewDto;
import com.service.user.model.enums.UserRole;
import com.service.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.example.models.UserEvent;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.assertj.core.api.Assertions;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
@Slf4j
public class UserServiceTest {


    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private MongoTemplate mongoTemplate;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private NewTopic topic;

    @Mock
    private KafkaTemplate<String, UserEvent> kafkaTemplate;

    @InjectMocks
    private UserService userService;

    private UserCreateDto userCreateDto;
    private UserUpdateDto userUpdateDto;
    private User user;
    private User userAfterUpdate;
    private UserViewDto userViewDtoAfterUpdate;
    private UserViewDto userViewDto;

    @BeforeEach
    public void init(){

        user = UserBuilder.build("66712b8cff196c3bba3d9cc7","Milos", "Lukic", "milo.test.ns@gmail.com", "064123123",
                "lozinka", UserRole.ADMIN, false, "093482094820984",
                new Date());

        userViewDto = UserViewDto.builder().userId("66712b8cff196c3bba3d9cc7").firstName("Milos")
                .lastName("Lukic").email("milo.test.ns@gmail.com")
                .userRole("ADMIN").contactInformation("064131231").build();

        userCreateDto = UserCreateDtoBuilder.build("Milos", "Lukic", "milo.test.ns@gmail.com",
                "064123123", "lozinka", UserRole.ADMIN);

        userUpdateDto = UserUpdateDtoBuilder.build("66712b8cff196c3bba3d9cc7", "Mis", "Lic",
                "milo.test.ns@mail.com", "0632323232");

        userAfterUpdate = UserBuilder.build("66712b8cff196c3bba3d9cc7","Mis", "Lic", "milo.test.ns@gmail.com", "062444222",
                "lozinka", UserRole.ADMIN, false, "093482094820984",
                new Date());

        userViewDtoAfterUpdate = UserViewDtoBuilder.build("66712b8cff196c3bba3d9cc7", "Mis", "Lic", "milo.test.ns@gmail.com",
                "062444222", UserRole.ADMIN);

    }

    @Test
    public void getAllUsersFail(){
        when(userRepository.findAll()).thenReturn(null);
        assertThrows(UserNotFoundException.class, ()-> userService.getAllUsers(0, 10));
    }


    @Test
    public void findUserByIdSuccessTest(){

        when(userRepository.findById(user.getUserId())).thenReturn(Optional.ofNullable(user));
        when(userMapper.toViewDtoFromUser(user)).thenReturn(userViewDto);


        UserViewDto dto = userService.getUserById(user.getUserId());
        Assertions.assertThat(dto).isNotNull();
        Assertions.assertThat(userViewDto).usingRecursiveAssertion().isEqualTo(dto);
    }


    @Test
    public void createUserSuccessTest(){

        when(userRepository.save(Mockito.any(User.class))).thenReturn(user);
        when(userMapper.toViewDtoFromUser(user)).thenReturn(userViewDto);
        when(userMapper.toUserFromCreate(userCreateDto)).thenReturn(user);


        UserViewDto dto = userService.createUser(userCreateDto);

        Assertions.assertThat(userViewDto).isEqualTo(dto);
    }


    @Test
    public void updateUserSuccessTest(){

        when(userRepository.findById(userUpdateDto.getUserId())).thenReturn(Optional.ofNullable(user));
        when(userMapper.toViewDtoFromUser(userAfterUpdate)).thenReturn(userViewDtoAfterUpdate);
        when(userRepository.save(Mockito.any(User.class))).thenReturn(userAfterUpdate);

        UserViewDto userViewAfterUpdate = userService.updateUser(userUpdateDto);

        Assertions.assertThat(userViewDtoAfterUpdate).isEqualTo(userViewAfterUpdate);
        Assertions.assertThat(user).isNotEqualTo(userAfterUpdate);
    }

    @Test
    public void updateUserPasswordSuccessTest(){

        UserPasswordUpdate userPasswordUpdate = UserPasswordUpdate.builder().userId("66712b8cff196c3bba3d9cc7").newPassword("lozinka2")
                .oldPassword("lozinka").build();

        User userAfter = user;
        userAfter.setPassword(userPasswordUpdate.getNewPassword());

        UserViewDto userViewDto = UserViewDto.builder().userId("66712b8cff196c3bba3d9cc7").firstName("Milos")
                .lastName("Lukic").email("milo.test.ns@gmail.com")
                .userRole("ADMIN").contactInformation("064131231").build();


        when(userRepository.findById(userPasswordUpdate.getUserId())).thenReturn(Optional.ofNullable(user));
        when(userRepository.save(Mockito.any(User.class))).thenReturn(userAfter);
        when(passwordEncoder.matches(userPasswordUpdate.getOldPassword(), user.getPassword())).thenReturn(true);
        when(userMapper.toViewDtoFromUser(userAfter)).thenReturn(userViewDto);

        UserViewDto dto = userService.updateUserPassword(userPasswordUpdate);

        Assertions.assertThat(userViewDto).isEqualTo(dto);
    }

    @Test
    public void deleteUserSuccess(){

        when(userRepository.existsById(user.getUserId())).thenReturn(true);
        doNothing().when(userRepository).deleteById(user.getUserId());

        String response = userService.deleteUser(user.getUserId());
        Assertions.assertThat(response).isEqualTo("Deleted user");
    }


    @Test
    public void deleteUserFail(){

        when(userRepository.existsById(user.getUserId())).thenReturn(false);

        assertThrows(UserNotFoundException.class, ()->userService.deleteUser(user.getUserId()));
    }



}
