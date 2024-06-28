package com.service.user.service;

import com.service.user.security.JwtTokenUtil;
import com.service.user.security.UserDetailsServiceCustom;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountLockedException;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtUtil;
    private final UserDetailsServiceCustom service;


    public String authenticate(@NonNull String username, @NonNull String password) throws Exception {

        try {
            log.info("Login tried");
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
            final UserDetails userDetails = service.loadUserByUsername(username);

            final String token = jwtUtil.generateToken(userDetails);
            log.info("Login success");
            return token;
        } catch (DisabledException e) {
            log.error("USER_DISABLED, Exception: {}", e.toString());
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            log.error("INVALID_CREDENTIALS, Exception: {}", e.toString());
            throw new BadCredentialsException("INVALID_CREDENTIALS", e);
        } catch (LockedException e){
            log.error("ACCOUNT_LOCKED_NOT_CONFIRMED, Exception: {}", e.toString());
            throw new LockedException("ACCOUNT_LOCKED", e);
        }
    }
}
