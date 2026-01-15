package com.social.horror_pool.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignUpRequest {
    @NotBlank(message = "Username cannot be blank")
    @Size(min = 5, max = 20, message = "Username must be 5-20 characters long")
    private String username;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    @Size(max = 50, message = "Email cannot be longer than 50 chars")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, max = 20, message = "Password must be 8-20 characters long")
    private String password;

    @NotBlank(message = "Please confirm the password")
    @Size(min = 8, max = 20, message = "Password must be 8-20 characters long")
    private String confirmPassword;
}
