package com.gymlog.controller;

import com.gymlog.dto.response.PagedResponse;
import com.gymlog.dto.response.PlatformStatsResponse;
import com.gymlog.dto.response.UserResponse;
import com.gymlog.repository.*;
import com.gymlog.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin", description = "Admin management operations")
public class AdminController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final WorkoutRepository workoutRepository;
    private final ExerciseRepository exerciseRepository;
    private final NutritionLogRepository nutritionLogRepository;

    @GetMapping("/stats")
    @Operation(summary = "Get platform statistics")
    public ResponseEntity<PlatformStatsResponse> getPlatformStats() {
        PlatformStatsResponse stats = PlatformStatsResponse.builder()
                .totalUsers(userRepository.count())
                .activeUsers(userRepository.countByActive(true))
                .totalWorkouts(workoutRepository.count())
                .totalExercises(exerciseRepository.count())
                .totalNutritionLogs(nutritionLogRepository.count())
                .build();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/users")
    @Operation(summary = "Get all users with pagination")
    public ResponseEntity<PagedResponse<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(userService.getAllUsers(page, size));
    }

    @PutMapping("/users/{id}/toggle-status")
    @Operation(summary = "Activate/deactivate a user")
    public ResponseEntity<UserResponse> toggleUserStatus(@PathVariable Long id) {
        return ResponseEntity.ok(userService.toggleUserStatus(id));
    }
}
