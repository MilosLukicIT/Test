package com.service.user.controller;


import com.service.user.model.dto.UserCreateDto;
import com.service.user.model.dto.UserPasswordUpdate;
import com.service.user.model.dto.UserUpdateDto;
import com.service.user.model.dto.UserViewDto;
import com.service.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.EmailNotification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService service;


    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserViewDto>> getAllUsers(@RequestParam(defaultValue = "0") int pageNumber,
                                                         @RequestParam(defaultValue = "10") int sizePerPage ){

        return ResponseEntity.ok(service.getAllUsers(pageNumber, sizePerPage));
    }

//    @AuthenticationPrincipal UserDetails userDetails, moze da se stavi u potpis metode zarad autentifikacije
    @GetMapping("/{userId}")
    public ResponseEntity<UserViewDto> getUserById(@PathVariable String userId){

        return ResponseEntity.ok(service.getUserById(userId));
    }

    @PostMapping
    public ResponseEntity<UserViewDto> createUser(@RequestBody UserCreateDto userDto) {

        return ResponseEntity.status(HttpStatus.CREATED).body(service.createUser(userDto));
    }

    @PatchMapping
    public ResponseEntity<UserViewDto> updateUser(@RequestBody UserUpdateDto userDto) {

        return ResponseEntity.ok(service.updateUser(userDto));
    }

    @PatchMapping("/password")
    public ResponseEntity<UserViewDto> updateUserPassword(@RequestBody UserPasswordUpdate userPasswordUpdate) {

        return ResponseEntity.ok(service.updateUserPassword(userPasswordUpdate));
    }


    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable String userId) {

        return ResponseEntity.status(HttpStatus.OK).body(service.deleteUser(userId));
    }


    @GetMapping("/confirm_registration/{id}")
    public ResponseEntity<String> confirmUserRegistration(@PathVariable String id){

        return ResponseEntity.status(HttpStatus.OK).body(service.confirmUserRegistration(id));
    }

    @GetMapping("/email_notification/{id}")
    public ResponseEntity<EmailNotification> getEmailNotification(@PathVariable String id){

        return ResponseEntity.ok(service.getEmailNotifications(id));
    }
}
