package com.social.horror_pool.controller;

import com.social.horror_pool.payload.MessageResponse;
import com.social.horror_pool.payload.SignInRequest;
import com.social.horror_pool.payload.SignUpRequest;
import com.social.horror_pool.payload.UserInfoResponse;
import com.social.horror_pool.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "Authentication", description = "Endpoints for user sign-in, sign-up, and profile access")
@RestController
@RequestMapping("/horrorpool")
public class AuthController {


    private final AuthService authService;

    public AuthController( AuthService authService) {
        this.authService = authService;
    }

    @Operation(
            summary = "User Sign In",
            description = "Authenticates the user and returns a JWT cookie. Accessible to all users."
    )
    @PostMapping("/public/signin")
    public ResponseEntity<?> signInUser(@Valid @RequestBody SignInRequest signInRequest) {
        return this.authService.signInUser(signInRequest.getUsername(), signInRequest.getPassword());
    }

    @Operation(
            summary = "User Registration",
            description = "Registers a new user with a username, email, and password. Accessible to all users."
    )
    @PostMapping("/public/signup")
    public ResponseEntity<MessageResponse> signUpNewUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        MessageResponse response = this.authService.signUpNewUser(signUpRequest.getUsername(), signUpRequest.getEmail(), signUpRequest.getPassword(), signUpRequest.getConfirmPassword());
        return new ResponseEntity<MessageResponse>(response, HttpStatus.CREATED);
    }

    @Operation(
            summary = "User Sign Out",
            description = "Signs the user out by removing the authentication cookie. Accessible to all users."
    )
    @PostMapping("/public/signout")
    public ResponseEntity<?> signOutUser() {
        return this.authService.signOutUser();
    }

    @Operation(
            summary = "Get Current Username",
            description = "Returns the username of the currently authenticated user. Requires authentication."
    )
    @GetMapping("/username")
    public Map<String, String> getCurrentUsername() {
        return Map.of("username", this.authService.getCurrentUsername());
    }

    @Operation(
            summary = "Get Current User Info",
            description = "Returns detailed user information for the currently authenticated user. Requires authentication."
    )
    @GetMapping("/user")
    public ResponseEntity<UserInfoResponse> fetchCurrentUserInfo() {
        UserInfoResponse response = this.authService.fetchCurrentUserInfo();
        return new ResponseEntity<UserInfoResponse>(response, HttpStatus.OK);
    }
}
