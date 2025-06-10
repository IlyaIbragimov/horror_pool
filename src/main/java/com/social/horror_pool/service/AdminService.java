package com.social.horror_pool.service;

import com.social.horror_pool.payload.UserInfoResponse;

public interface AdminService {
    UserInfoResponse changeUserLockStatus(Long userId);

    UserInfoResponse disableUser(Long userId);
}
