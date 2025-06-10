package com.social.horror_pool.service.impl;

import com.social.horror_pool.exception.ResourceNotFoundException;
import com.social.horror_pool.model.User;
import com.social.horror_pool.payload.UserInfoResponse;
import com.social.horror_pool.repository.UserRepository;
import com.social.horror_pool.service.AdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;

    private final Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);

    public AdminServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserInfoResponse changeUserLockStatus(Long userId) {

        User userToModify = this.userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        userToModify.setLocked(!userToModify.isLocked());
        this.userRepository.save(userToModify);

        this.logger.info("Admin toggled lock status for user: {}", userToModify.getUsername());

        return getUserInfoResponse(userToModify);
    }

    @Override
    public UserInfoResponse disableUser(Long userId) {

        User userToModify = this.userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        userToModify.setEnabled(!userToModify.isEnabled());
        this.userRepository.save(userToModify);

        this.logger.info("Admin toggled enable status for user: {}", userToModify.getUsername());

        return getUserInfoResponse(userToModify);
    }

    private UserInfoResponse getUserInfoResponse(User user) {
        Set<String> roles = user.getRoles().stream()
                .map(role -> role.getRoleName().name()).collect(Collectors.toSet());

        UserInfoResponse response = new UserInfoResponse();
        response.setUsername(user.getUsername());
        response.setRoles(roles);
        response.setLocked(user.isLocked());
        response.setEmail(user.getEmail());
        response.setUserId(user.getUserId());
        response.setEnabled(user.isEnabled());

        return response;
    }
}
