package com.social.horror_pool.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {

    private Long userId;
    private String username;
    private String email;
    private Set<String> roles;
    private boolean enabled;
    private boolean locked;

    public UserInfoResponse(Long userId, String username, Set<String> roles, boolean locked) {
        this.userId = userId;
        this.username = username;
        this.roles = roles;
        this.locked = locked;
    }
}
