package com.gymlog.service.impl;

import com.gymlog.dto.request.UpdateProfileRequest;
import com.gymlog.dto.response.PagedResponse;
import com.gymlog.dto.response.UserResponse;
import com.gymlog.entity.User;
import com.gymlog.exception.ResourceNotFoundException;
import com.gymlog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements com.gymlog.service.UserService {

    private final UserRepository userRepository;

    public UserResponse getProfile(String email) {
        User user = findByEmail(email);
        return mapToResponse(user);
    }

    @Transactional
    public UserResponse updateProfile(String email, UpdateProfileRequest request) {
        User user = findByEmail(email);

        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getHeight() != null) user.setHeight(request.getHeight());
        if (request.getCurrentWeight() != null) user.setCurrentWeight(request.getCurrentWeight());
        if (request.getDateOfBirth() != null) user.setDateOfBirth(request.getDateOfBirth());
        if (request.getGender() != null) user.setGender(request.getGender());
        if (request.getDailyCalorieTarget() != null) user.setDailyCalorieTarget(request.getDailyCalorieTarget());

        user = userRepository.save(user);
        log.info("Profile updated for user: {}", email);
        return mapToResponse(user);
    }

    public PagedResponse<UserResponse> getAllUsers(int page, int size) {
        Page<User> userPage = userRepository.findAll(
                PageRequest.of(page, size, Sort.by("createdAt").descending()));

        List<UserResponse> users = userPage.getContent().stream()
                .map(this::mapToResponse)
                .toList();

        return PagedResponse.<UserResponse>builder()
                .content(users)
                .page(userPage.getNumber())
                .size(userPage.getSize())
                .totalElements(userPage.getTotalElements())
                .totalPages(userPage.getTotalPages())
                .last(userPage.isLast())
                .build();
    }

    @Transactional
    public UserResponse toggleUserStatus(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        user.setActive(!user.getActive());
        user = userRepository.save(user);
        log.info("User {} status toggled to: {}", userId, user.getActive());
        return mapToResponse(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole().name())
                .height(user.getHeight())
                .currentWeight(user.getCurrentWeight())
                .dateOfBirth(user.getDateOfBirth())
                .gender(user.getGender())
                .dailyCalorieTarget(user.getDailyCalorieTarget())
                .active(user.getActive())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
