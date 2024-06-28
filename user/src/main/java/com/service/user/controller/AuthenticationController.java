package com.service.user.controller;

import com.service.user.model.LoginRequest;
import com.service.user.security.JwtTokenUtil;
import com.service.user.security.UserDetailsServiceCustom;
import com.service.user.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/authentication")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) throws Exception {

        return ResponseEntity.ok(authenticationService.authenticate(loginRequest.getEmail(), loginRequest.getPassword()));
    }
}
