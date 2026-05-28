package com.social.horror_pool.configuration;

import com.social.horror_pool.exception.APIException;
import com.social.horror_pool.model.Role;
import com.social.horror_pool.model.User;
import com.social.horror_pool.repository.RoleRepository;
import com.social.horror_pool.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final boolean adminBootstrapEnabled;
    private final String adminPassword;
    private final String adminUsername;
    private final String adminEmail;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder,
                           RoleRepository roleRepository,
                           @Value("${app.bootstrap.admin.enabled:false}") boolean adminBootstrapEnabled,
                           @Value("${app.bootstrap.admin.password:}") String adminPassword,
                           @Value("${app.bootstrap.admin.username:}") String adminUsername,
                           @Value("${app.bootstrap.admin.email:}") String adminEmail) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.adminBootstrapEnabled = adminBootstrapEnabled;
        this.adminPassword = adminPassword;
        this.adminUsername = adminUsername;
        this.adminEmail = adminEmail;
    }

    @Override
    public void run(String... args) throws Exception {
        if (this.roleRepository.count() == 0) {
            Role userRole = new Role(RoleName.ROLE_USER);
            Role adminRole = new Role(RoleName.ROLE_ADMIN);
            this.roleRepository.saveAll(List.of(userRole, adminRole));
        }

        if (adminBootstrapEnabled && userRepository.count() == 0) {
            if (isBlank(adminUsername)) {
                throw new IllegalStateException("Admin bootstrap username must be set");
            }

            if (isBlank(adminEmail)) {
                throw new IllegalStateException("Admin bootstrap email must be set");
            }

            if (adminPassword == null || adminPassword.length() < 12) {
                throw new IllegalStateException("Admin bootstrap password must be set and at least 12 characters long");
            }

            Role adminRole = this.roleRepository.findByRoleName(RoleName.ROLE_ADMIN)
                    .orElseThrow(() -> new APIException("No admin role found"));

            User admin = new User(adminUsername, adminEmail, passwordEncoder.encode(adminPassword));

            admin.setRoles(Set.of(adminRole));
            userRepository.save(admin);
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
