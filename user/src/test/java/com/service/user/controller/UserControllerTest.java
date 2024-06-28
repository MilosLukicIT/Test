package com.service.user.controller;

import com.service.user.ContainerBaseTest;
import com.service.user.builders.*;
import com.service.user.model.LoginRequest;
import com.service.user.model.User;
import com.service.user.model.dto.UserCreateDto;
import com.service.user.model.dto.UserPasswordUpdate;
import com.service.user.model.dto.UserUpdateDto;
import com.service.user.model.dto.UserViewDto;
import com.service.user.model.enums.UserRole;
import com.service.user.model.exception.ErrorResponseMessage;
import com.service.user.models.UserPasswordControl;
import com.service.user.repository.UserRepository;
import com.service.user.security.JwtTokenUtil;
import com.service.user.security.UserDetailsServiceCustom;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import org.assertj.core.api.Assertions;
import org.example.model.EmailNotification;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class UserControllerTest extends ContainerBaseTest {


    private static final Logger log = LoggerFactory.getLogger(UserControllerTest.class);

    @Autowired
    private JwtTokenUtil tokenUtil;

    @Autowired
    private UserDetailsServiceCustom userDetailsServiceCustom;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String token;
    private UserCreateDto userCreateDto;
    private UserUpdateDto userUpdateDto;
    private List<String> userIds;
    private User user;
    private User userTestConfirmedEmail;
    private User userTestTimeExpiredConfirmRegistration;
    private UserPasswordUpdate userPasswordUpdate;

    @BeforeEach
    public void init() {

        userRepository.deleteAll();
        userIds = new ArrayList<>();

        for (int i = 0; i < 5; i++) {

            user = UserBuilder.build("Milos", "Lukic", "milos" + UUID.randomUUID() + "@mail.com", "064123123",
                    passwordEncoder.encode("lozinka"), UserRole.ADMIN, true, "093482094820984",
                    new Date());

            this.user = userRepository.save(user);
            userIds.add(this.user.getUserId());
        }

        userTestConfirmedEmail = UserBuilder.build("Milos", "Lukic", "milos" + UUID.randomUUID() + "@mail.com", "064123123",
                passwordEncoder.encode("lozinka"), UserRole.ADMIN, false, "093482qwvxc094820984",
                new Date());

        userTestTimeExpiredConfirmRegistration = UserBuilder.build("Milos", "Lukic", "milos" + UUID.randomUUID() + "@mail.com", "064123123",
                passwordEncoder.encode("lozinka"), UserRole.ADMIN, false, "09348209482vxcvx0984",
                new Date(new Date().getTime() - TimeUnit.HOURS.toMillis(1) * 6));

        userRepository.save(userTestConfirmedEmail);
        userRepository.save(userTestTimeExpiredConfirmRegistration);

        UserDetails userDetails = userDetailsServiceCustom.loadUserByUsername(user.getEmail());
        token = tokenUtil.generateToken(userDetails);

        userCreateDto = UserCreateDtoBuilder.build("Milos", "Lukic", "milo.test.ns@gmail.com",
                "064123123", "lozinka", UserRole.ADMIN);

        userUpdateDto = UserUpdateDtoBuilder.build(userIds.get(1), "Mis", "Lic",
                "milo.test.ns@mail.com", "0632323232");

        userPasswordUpdate = UserPasswordUpdate.builder().userId(user.getUserId()).newPassword("lozinka2")
                .oldPassword("lozinka").build();
    }

    @AfterEach
    public void after() {
        userRepository.deleteAll();
    }

    @Test
    void getAllUsers() throws Exception {

        List<UserViewDto> users = given().header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .when().get("/api/v1/user")
                .then().assertThat().statusCode(200)
                .extract().as(new TypeRef<>() {
                });

        List<UserViewDto> userFromRepo = userRepository.findAll()
                .stream().map(UserViewDtoBuilder::build)
                .toList();

        Assertions.assertThat(users.size()).isEqualTo(userFromRepo.size());

        for (int i = 0; i < users.size(); i++) {
            Assertions.assertThat(users.get(i)).isEqualTo(userFromRepo.get(i));
        }
    }


    @Test
    void getOneUserAuthenticated() throws Exception {

        UserViewDto user = given().header(HttpHeaders.AUTHORIZATION, "Bearer " + token).
                when().get("/api/v1/user/" + userIds.get(1)).then().
                assertThat().statusCode(200).
                extract().as(UserViewDto.class);

        UserViewDto userFromRepo = UserViewDtoBuilder.build(userRepository.findById(userIds.get(1)).get());

        Assertions.assertThat(user).isEqualTo(userFromRepo);
    }

    @Test
    void getOneUserUnauthenticated() throws Exception {

        given().
                when().get("/api/v1/user/66718468bd995f2bb477023c")
                .then().assertThat().statusCode(403);
    }

    @Test
    void getOneUserNotFound() throws Exception {

        ErrorResponseMessage response = given().header(HttpHeaders.AUTHORIZATION, "Bearer " + token).
                when().get("/api/v1/user/66718468bd995f2bb47704")
                .then().assertThat().statusCode(404).extract().as(ErrorResponseMessage.class);

        Assertions.assertThat(response.getMessage()).isEqualTo("Didn't find any user");
    }


    @Test
    void loginSuccess() throws Exception {

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(user.getEmail());
        loginRequest.setPassword("lozinka");

        given().body(loginRequest)
                .when().contentType(ContentType.JSON).post("/api/v1/authentication")
                .then().assertThat().statusCode(200);
    }

    @Test
    void loginFailBadCredentials() throws Exception {

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(user.getEmail());
        loginRequest.setPassword("lozinka2");

        ErrorResponseMessage response = given().body(loginRequest)
                .when().contentType(ContentType.JSON).post("/api/v1/authentication")
                .then().assertThat().statusCode(403).extract().as(ErrorResponseMessage.class);

        Assertions.assertThat(response.getMessage()).isEqualTo("INVALID_CREDENTIALS");
    }

    @Test
    void loginFailAccountNotConfirmed() throws Exception {

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(userTestConfirmedEmail.getEmail());
        loginRequest.setPassword(userTestConfirmedEmail.getPassword());

        ErrorResponseMessage response = given().body(loginRequest)
                .when().contentType(ContentType.JSON).post("/api/v1/authentication")
                .then().assertThat().statusCode(403).extract().as(ErrorResponseMessage.class);

        Assertions.assertThat(response.getMessage()).isEqualTo("ACCOUNT_LOCKED");
    }

    @Test
    void createUser() throws Exception {

        UserViewDto user = given().contentType(ContentType.JSON).body(userCreateDto)
                .when().post("/api/v1/user").then()
                .assertThat().statusCode(201).extract().as(UserViewDto.class);

        UserViewDto userFromRepo = UserViewDtoBuilder.build(userRepository.findById(user.getUserId()).get());

        Assertions.assertThat(user).isEqualTo(userFromRepo);
    }

    @Test
    void createUserEmailDuplicate() throws Exception {

        userCreateDto.setEmail(user.getEmail());

        ErrorResponseMessage response = given().body(userCreateDto).contentType(ContentType.JSON)
                .when().post("/api/v1/user")
                .then().assertThat().statusCode(400).extract().as(ErrorResponseMessage.class);

        Assertions.assertThat(response.getMessage()).isEqualTo("Different email");
    }

    @Test
    void updateUser() throws Exception {

        UserViewDto user = given().header(HttpHeaders.AUTHORIZATION, "Bearer " + token).body(userUpdateDto).contentType(ContentType.JSON)
                .when().patch("/api/v1/user").then().
                assertThat().statusCode(200).extract().as(UserViewDto.class);

        UserViewDto userFromRepo = UserViewDtoBuilder.build(userRepository.findById(user.getUserId()).get());

        Assertions.assertThat(user).isEqualTo(userFromRepo);
    }

    @Test
    void updateUserNotFound() throws Exception {

        userUpdateDto.setUserId("oqif023fh0239fhn02q3if");

        ErrorResponseMessage response = given().header(HttpHeaders.AUTHORIZATION, "Bearer " + token).body(userUpdateDto).contentType(ContentType.JSON)
                .when().patch("/api/v1/user").then().
                assertThat().statusCode(404).extract().as(ErrorResponseMessage.class);

        UserViewDto userFromRepo = UserViewDtoBuilder.build(userRepository.findById(user.getUserId()).get());

        Assertions.assertThat(response.getMessage()).isEqualTo("User doesn't exist");
    }

    @Test
    void updatePassword() {

        given().contentType(ContentType.JSON).body(userPasswordUpdate)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .when().patch("/api/v1/user/password")
                .then().assertThat().statusCode(200);

        UserPasswordControl passwordControl = UserPasswordControlBuilder.build(userRepository.findById(userPasswordUpdate.getUserId()).get());

        Assertions.assertThat(passwordEncoder.matches(userPasswordUpdate.getNewPassword(), passwordControl.getPassword()))
                .isEqualTo(true);

    }

    @Test
    void updatePasswordWrongOldPassword() {

        userPasswordUpdate.setOldPassword("wrong");
        ErrorResponseMessage response = given().contentType(ContentType.JSON).body(userPasswordUpdate)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .when().patch("/api/v1/user/password")
                .then().assertThat().statusCode(400).extract().as(ErrorResponseMessage.class);

        Assertions.assertThat(response.getMessage()).isEqualTo("Bad credentials");
    }

    @Test
    void updatePasswordUserNotFound() {

        userPasswordUpdate.setUserId("02h024ht0384ht3084h0h4");
        ErrorResponseMessage response = given().contentType(ContentType.JSON).body(userPasswordUpdate)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .when().patch("/api/v1/user/password")
                .then().assertThat().statusCode(404).extract().as(ErrorResponseMessage.class);

        Assertions.assertThat(response.getMessage()).isEqualTo("User doesn't exist");
    }


    @Test
    void deleteUser() {

        given().header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .when().delete("/api/v1/user/" + userIds.get(3))
                .then().assertThat().statusCode(200);


        User userAfterDelete = userRepository.findById(userIds.get(3)).orElse(null);
        Assertions.assertThat(userAfterDelete).isNull();
    }

    @Test
    void deleteUserFail() {

        ErrorResponseMessage response = given().header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .when().delete("/api/v1/user/01h3r923hr293r8h2938hr2q")
                .then().assertThat().statusCode(404).extract().as(ErrorResponseMessage.class);

        Assertions.assertThat(response.getMessage()).isEqualTo("User doesn't exist");
    }


    @Test
    void confirmEmailRegistration() {

        String response = given().
                when().get("/api/v1/user/confirm_registration/" + userTestConfirmedEmail.getConfirmationToken())
                .then().assertThat().statusCode(200).extract().asString();

        Assertions.assertThat(response).isEqualTo("Confirmed registration");
    }

    @Test
    void confirmEmailRegistrationFail() {

        ErrorResponseMessage response = given().
                when().get("/api/v1/user/confirm_registration/12312313w121w123123w1")
                .then().assertThat().statusCode(404).extract().as(ErrorResponseMessage.class);

        Assertions.assertThat(response.getMessage()).isEqualTo("User doesn't exist");
    }

    @Test
    void confirmEmailRegistrationAlreadyRegister() {

        String response = given().
                when().get("/api/v1/user/confirm_registration/" + user.getConfirmationToken())
                .then().assertThat().statusCode(200).extract().asString();

        Assertions.assertThat(response).isEqualTo("Registration is already confirmed");

    }

    @Test
    void confirmEmailRegistrationTimePassed() {

        String response = given().
                when().get("/api/v1/user/confirm_registration/" + userTestTimeExpiredConfirmRegistration.getConfirmationToken())
                .then().assertThat().statusCode(200).extract().asString();

        User userAfter = userRepository.findById(userTestTimeExpiredConfirmRegistration.getUserId()).orElse(null);

        Assertions.assertThat(response).isEqualTo("Registration confirmation time is over. We have sent you a new validation mail");
        assert userAfter != null;
        Assertions.assertThat(userTestTimeExpiredConfirmRegistration.getConfirmationTimeStamp()).isNotEqualTo(userAfter.getConfirmationTimeStamp());
        Assertions.assertThat(userTestTimeExpiredConfirmRegistration.getConfirmationToken()).isNotEqualTo(userAfter.getConfirmationToken());
    }

    @Test
    void getEmailNotification(){
        EmailNotification response = given().header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .when().get("/api/v1/user/email_notification/667bb315f7cb9c6ca5dffb0c")
                .then().assertThat().statusCode(200).extract().as(EmailNotification.class);

        Assertions.assertThat(response.getUserId()).isEqualTo("667bb315f7cb9c6ca5dffb0c");
        Assertions.assertThat(response.getMessage()).isEqualTo("User confirmed successfully");
    }

    @Test
    void getEmailNotificationNoUserFound(){
        given().header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .when().get("/api/v1/user/email_notification/667bb315f7cb9c6ca1dffb0c")
                .then().assertThat().statusCode(404);
    }


}