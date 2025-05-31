package com.social.horror_pool.controller;

import com.social.horror_pool.payload.MessageResponse;
import com.social.horror_pool.payload.SignInRequest;
import com.social.horror_pool.payload.SignUpRequest;
import com.social.horror_pool.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/horrorpool")
public class AuthController {


    private final AuthService authService;

    public AuthController( AuthService authService) {
        this.authService = authService;
    }



    @PostMapping("/signin")
    public ResponseEntity<?> signInUser(@Valid @RequestBody SignInRequest signInRequest) {
        return this.authService.signInUser(signInRequest.getUsername(), signInRequest.getPassword());
    }

    @PostMapping("/signup")
    public ResponseEntity<MessageResponse> signUpNewUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        MessageResponse response = this.authService.signUpNewUser(signUpRequest.getUsername(), signUpRequest.getEmail(), signUpRequest.getPassword(), signUpRequest.getConfirmPassword());
        return new ResponseEntity<MessageResponse>(response, HttpStatus.CREATED);
    }
    @PostMapping("/signout")
    public ResponseEntity<?> signOutUser() {
        return this.authService.signOutUser();
    }
}
