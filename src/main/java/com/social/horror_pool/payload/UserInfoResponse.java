package com.social.horror_pool.payload;

import lombok.Data;

import java.util.Set;
@Data
public class UserInfoResponse {

    private Long userId;
    private String username;
    private Set<String> roles;

    public UserInfoResponse(Long userId, String username, Set<String> roles) {
        this.userId = userId;
        this.username = username;
        this.roles = roles;
    }
}
