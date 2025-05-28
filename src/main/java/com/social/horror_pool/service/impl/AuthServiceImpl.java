package com.social.horror_pool.service.impl;

import com.social.horror_pool.configuration.RoleName;
import com.social.horror_pool.exception.APIException;
import com.social.horror_pool.model.Role;
import com.social.horror_pool.model.User;
import com.social.horror_pool.payload.MessageResponse;
import com.social.horror_pool.repository.RoleRepository;
import com.social.horror_pool.repository.UserRepository;
import com.social.horror_pool.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private AuthenticationManager authenticationManager;

    public AuthServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public MessageResponse signUpNewUser(String username, String email, String password, String confirmPassword) {
        if (userRepository.existsByUsername(username))
            return  new MessageResponse("Username is already taken");

        if (userRepository.existsByEmail(email))
            return  new MessageResponse("Email is already taken");

        if (!password.equals(confirmPassword)) {
            return new MessageResponse("Passwords do not match");
        }

        User user = new User(username, email, this.passwordEncoder.encode(password));

        Role role = this.roleRepository.findByRoleName(RoleName.ROLE_USER)
                        .orElseThrow(() -> new APIException("Error: Role not found"));

        user.setRoles(Set.of(role));
        userRepository.save(user);
        return new MessageResponse("User successfully created");
    }
}
