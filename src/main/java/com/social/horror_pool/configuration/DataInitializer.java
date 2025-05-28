package com.social.horror_pool.configuration;

import com.social.horror_pool.model.Role;
import com.social.horror_pool.model.User;
import com.social.horror_pool.repository.RoleRepository;
import com.social.horror_pool.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }


    @Override
    public void run(String... args) throws Exception {
        if (this.roleRepository.count() == 0) {
            Role userRole = new Role(RoleName.ROLE_USER);
            Role adminRole = new Role(RoleName.ROLE_ADMIN);
            this.roleRepository.saveAll(List.of(userRole, adminRole));
        }

        if (this.userRepository.count() == 0) {
            User admin = new User("admin", "admin@example.com", passwordEncoder.encode("adminPassword"));
            this.userRepository.save(admin);
        }
    }
}
