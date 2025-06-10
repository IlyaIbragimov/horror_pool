package com.social.horror_pool.controller;

import com.social.horror_pool.payload.MessageResponse;
import com.social.horror_pool.payload.SignInRequest;
import com.social.horror_pool.payload.SignUpRequest;
import com.social.horror_pool.payload.UserInfoResponse;
import com.social.horror_pool.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/horrorpool")
public class AuthController {


    private final AuthService authService;

    public AuthController( AuthService authService) {
        this.authService = authService;
    }



    @PostMapping("/public/signin")
    public ResponseEntity<?> signInUser(@Valid @RequestBody SignInRequest signInRequest) {
        return this.authService.signInUser(signInRequest.getUsername(), signInRequest.getPassword());
    }

    @PostMapping("/public/signup")
    public ResponseEntity<MessageResponse> signUpNewUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        MessageResponse response = this.authService.signUpNewUser(signUpRequest.getUsername(), signUpRequest.getEmail(), signUpRequest.getPassword(), signUpRequest.getConfirmPassword());
        return new ResponseEntity<MessageResponse>(response, HttpStatus.CREATED);
    }
    @PostMapping("/public/signout")
    public ResponseEntity<?> signOutUser() {
        return this.authService.signOutUser();
    }
    @GetMapping("/username")
    public String getCurrentUsername() {
        return this.authService.getCurrentUsername();
    }
    @GetMapping("/user")
    public ResponseEntity<UserInfoResponse> fetchCurrentUserInfo() {
        UserInfoResponse response = this.authService.fetchCurrentUserInfo();
        return new ResponseEntity<UserInfoResponse>(response, HttpStatus.OK);
    }
}
