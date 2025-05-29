package com.social.horror_pool.service.impl;

import com.social.horror_pool.configuration.RoleName;
import com.social.horror_pool.exception.APIException;
import com.social.horror_pool.model.Role;
import com.social.horror_pool.model.User;
import com.social.horror_pool.payload.MessageResponse;
import com.social.horror_pool.repository.RoleRepository;
import com.social.horror_pool.repository.UserRepository;
import com.social.horror_pool.service.AuthService;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

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
                        .orElseThrow(() -> new APIException("Error: Role " + RoleName.ROLE_USER + " not found"));

        user.setRoles(Set.of(role));
        userRepository.save(user);
        return new MessageResponse("User successfully created");
    }

    @Override
    public MessageResponse signInUser(String username, String password) {
        try {
            Authentication authentication = this.authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(username, password));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return new MessageResponse("User successfully logged in");

        } catch (BadCredentialsException e) {
            return new MessageResponse("Invalid username or password");
        } catch (DisabledException e) {
            return new MessageResponse("Your account is disabled");
        } catch (LockedException e) {
            return new MessageResponse("Your account is locked");
        }
    }
}
