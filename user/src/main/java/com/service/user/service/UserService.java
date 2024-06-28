package com.service.user.service;

import com.service.user.exceptions.UserNotFoundException;
import com.service.user.exceptions.WrongUserCredentials;
import com.service.user.handler.CustomStompSessionHandler;
import com.service.user.mapper.UserMapper;
import com.service.user.model.EmailNotificationConfirmRegistration;
import com.service.user.model.User;
import com.service.user.model.dto.UserCreateDto;
import com.service.user.model.dto.UserPasswordUpdate;
import com.service.user.model.dto.UserUpdateDto;
import com.service.user.model.dto.UserViewDto;
import com.service.user.model.enums.UserRole;
import com.service.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.example.EmailNotificationClient;
import org.example.model.EmailNotification;
import org.example.models.UserEvent;
import org.slf4j.event.Level;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder encoder;
    private final UserMapper userMapper;
    private final MongoTemplate mongoTemplate;
    private final NewTopic topic;
    private final WebSocketStompClient webSocketStompClient;
    private final EmailNotificationClient emailNotificationClient;

    private final KafkaTemplate<String, UserEvent> kafkaTemplate;

    public List<UserViewDto> getAllUsers(int pageNumber, int sizePerPage) {

        Pageable page = PageRequest.of(pageNumber, sizePerPage);

        List<UserViewDto> users = userMapper.toViewPage(repository.findAll(page));

        if(users.isEmpty()){
            log.error("There are no users in base");
            throw new UserNotFoundException("No users in base");
        }
        log.info("Returned a list of users, Log level: {}", Level.INFO);
        return users;
    }

    public UserViewDto getUserById(String userId) {

        User user = repository.findById(userId).orElse(null);
        if(Objects.isNull(user)){
            log.error("No users in base");
            throw new UserNotFoundException("Didn't find any user");
        }

        UserViewDto dto = userMapper.toViewDtoFromUser(user);
        log.info("User found, Log level: {}", Level.INFO);
        return dto;
    }

    public UserViewDto createUser(UserCreateDto userDto) {

        if(existsByEmail(userDto.getEmail()))
        {
            log.error("Email duplicate, Log level {}", Level.ERROR);
            throw  new WrongUserCredentials("Different email");
        }
        userDto.setPassword(encoder.encode(userDto.getPassword()));
        User user = userMapper.toUserFromCreate(userDto);
        user.setConfirmationToken(String.valueOf(UUID.randomUUID()));

        repository.save(user);

        sendEmail(user);

        UserViewDto dto = userMapper.toViewDtoFromUser(user);
        log.info("Created new user, Log level: {}", Level.INFO);

        return dto;
    }

    public UserViewDto updateUser(UserUpdateDto userDto) {

        User user = repository.findById(userDto.getUserId()).orElse(null);
        if(Objects.isNull(user)){
            log.error("User with id: {}, doesn't exist", userDto.getUserId());
            throw new UserNotFoundException("User doesn't exist");
        }
        userDto.toUser(user);
        UserViewDto dto = userMapper.toViewDtoFromUser(repository.save(user));
        log.info("Updated user with id: {}", user.getUserId());
        return dto;
    }

    public UserViewDto updateUserPassword(UserPasswordUpdate userPasswordDto) {

        User user = repository.findById(userPasswordDto.getUserId()).orElse(null);

        if(Objects.isNull(user)){
            log.error("User doesn't exist");
            throw new UserNotFoundException("User doesn't exist");
        }

        if(!encoder.matches(userPasswordDto.getOldPassword(),user.getPassword())){
            log.error("Credentials not compatible: {}", userPasswordDto.getUserId());
            throw new WrongUserCredentials("Bad credentials");
        }
        user.setPassword(encoder.encode(userPasswordDto.getNewPassword()));
        repository.save(user);

        UserViewDto dto = userMapper.toViewDtoFromUser(user);
        log.info("Updated password for user: {}", user.getUserId());
        return dto;
    }

    public String deleteUser(String userId) {

        if(!repository.existsById(userId)){
            log.error("No users in base to delete with id: {}", userId);
            throw new UserNotFoundException("User doesn't exist");
        }
        repository.deleteById(userId);
        log.info("Deleted user with id: {}", userId);
        return "Deleted user";
    }

    public boolean existsByEmail(String email) {
        return getUserByEmail(email) != null;
    }

    public String confirmUserRegistration(String token) {

        User user = getUserByConfirmationToken(token);
        if(Objects.isNull(user)){
            log.error("User with confirmation token: {}, not found", token);
            throw new UserNotFoundException("User doesn't exist");
        }

        if(user.isConfirmedEmail()){
            log.info("Email is already confirmed");
            return "Registration is already confirmed";
        }

        long timeDifference = (new Date().getTime() - user.getConfirmationTimeStamp().getTime())/(1000*60);

        if(timeDifference > 30){
            log.error("Registration confirmation time is over. Send a new validation mail");
            user.setConfirmationTimeStamp(new Date());
            user.setConfirmationToken(String.valueOf(UUID.randomUUID()));
            repository.save(user);
            sendEmail(user);
            return "Registration confirmation time is over. We have sent you a new validation mail";
        }

        user.setConfirmedEmail(true);
        repository.save(user);

        StompSessionHandler sessionHandler = new CustomStompSessionHandler();

        try {
            log.info("Started connecting to websocket");
            User admin = getUserByUserRole(UserRole.ADMIN);

            log.info(admin.getUserId());
            EmailNotificationConfirmRegistration email = EmailNotificationConfirmRegistration.builder()
                    .adminId(admin.getUserId())
                            .message("User confirmed successfully")
                                    .timeRegistered(new Date())
                                            .userId(user.getUserId()).build();

            StompSession stompSession = webSocketStompClient.connectAsync("ws://localhost:8081/ws", sessionHandler).get();
            stompSession.send("/app/message", email);

            log.info("Sent a message to the email notification trough service to alert admins");

        } catch (Exception e){
            log.info(e.getMessage());
        }


        log.info("Email confirmed for user: {}", user.getUserId());
        return "Confirmed registration";
    }

    public User getUserByEmail(String email){

        Query query = new Query();
        query.addCriteria(Criteria.where("email").is(email));
        return mongoTemplate.findOne(query, User.class);
    }

    public User getUserByConfirmationToken(String token){

        Query query = new Query();
        query.addCriteria(Criteria.where("confirmationToken").is(token));
        return mongoTemplate.findOne(query, User.class);
    }

    public User getUserByUserRole(UserRole role){
        Query query = new Query();
        query.addCriteria(Criteria.where("userRole").is(role));
        return mongoTemplate.findOne(query, User.class);
    }

    private void sendEmail(User user){

        UserEvent userEvent = new UserEvent();
        userEvent.setStatus("PENDING");
        userEvent.setMessage("Please confirm your registration");
        userEvent.setEmail(user.getEmail());
        userEvent.setConfirmationToken(user.getConfirmationToken());
        userEvent.setFirstName(user.getFirstName());
        userEvent.setLastName(user.getLastName());

        Message<UserEvent> message = MessageBuilder
                .withPayload(userEvent)
                .setHeader(KafkaHeaders.TOPIC,topic.name() )
                .build();
        kafkaTemplate.send(message);

        log.info("Email sent");
    }

    public EmailNotification getEmailNotifications(String userId){

        ResponseEntity<EmailNotification> notification = emailNotificationClient.getOneNotification(userId);

        log.info("Notification returned");
        return notification.getBody();
    }
}
