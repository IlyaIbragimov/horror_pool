package com.social.horror_pool.controller;

import com.social.horror_pool.payload.UserInfoResponse;
import com.social.horror_pool.service.AdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/horrorpool/admin")
public class AdminController {

    final private AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PutMapping("/user/{userId}/lock")
    public ResponseEntity<UserInfoResponse> changeUserLockStatus(@PathVariable Long userId) {
        UserInfoResponse response = this.adminService.changeUserLockStatus(userId);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
}
