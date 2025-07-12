package com.social.horror_pool.controller;

import com.social.horror_pool.payload.UserInfoResponse;
import com.social.horror_pool.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Admin", description = "Endpoints for user management by administrators")
@RestController
@RequestMapping("/horrorpool/admin")
public class AdminController {

    final private AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @Operation(
            summary = "Lock or unlock a user account",
            description = "Toggle the lock status of a user account by user ID. Only accessible to administrators."
    )
    @PutMapping("/user/{userId}/lock")
    public ResponseEntity<UserInfoResponse> changeUserLockStatus(@PathVariable Long userId) {
        UserInfoResponse response = this.adminService.changeUserLockStatus(userId);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @Operation(
            summary = "Disable or enable a user account",
            description = "Toggle the enabled status of a user account by user ID. Only accessible to administrators."
    )
    @PutMapping("/user/{userId}/disable")
    public ResponseEntity<UserInfoResponse> disableUser(@PathVariable Long userId) {
        UserInfoResponse response = this.adminService.disableUser(userId);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }




}
