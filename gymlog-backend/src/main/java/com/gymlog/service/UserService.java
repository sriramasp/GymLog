package com.gymlog.service;

import com.gymlog.dto.request.UpdateProfileRequest;
import com.gymlog.dto.response.PagedResponse;
import com.gymlog.dto.response.UserResponse;
import com.gymlog.entity.User;

public interface UserService {
    UserResponse getProfile(String email);
    UserResponse updateProfile(String email, UpdateProfileRequest request);
    PagedResponse<UserResponse> getAllUsers(int page, int size);
    UserResponse toggleUserStatus(Long userId);
    User findByEmail(String email);
}
