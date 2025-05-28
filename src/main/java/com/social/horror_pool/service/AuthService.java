package com.social.horror_pool.service;

import com.social.horror_pool.payload.MessageResponse;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    MessageResponse signUpNewUser(@NotBlank @Size(min = 5, max = 20) String username, @NotBlank @Email @Size(max = 50) String email, @NotBlank @Size(min = 8, max = 20) String password, String confirmPassword);
}
