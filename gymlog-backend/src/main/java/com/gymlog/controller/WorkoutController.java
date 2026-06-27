package com.gymlog.controller;

import com.gymlog.dto.request.WorkoutRequest;
import com.gymlog.dto.response.ApiResponse;
import com.gymlog.dto.response.PagedResponse;
import com.gymlog.dto.response.WorkoutResponse;
import com.gymlog.service.impl.WorkoutServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/workouts")
@RequiredArgsConstructor
@Tag(name = "Workouts", description = "Workout tracking and management")
public class WorkoutController {

    private final WorkoutServiceImpl workoutService;

    @PostMapping
    @Operation(summary = "Create a new workout")
    public ResponseEntity<WorkoutResponse> createWorkout(Authentication authentication,
                                                          @Valid @RequestBody WorkoutRequest request) {
        return new ResponseEntity<>(workoutService.createWorkout(authentication.getName(), request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get workout by ID")
    public ResponseEntity<WorkoutResponse> getWorkout(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(workoutService.getWorkoutById(id, authentication.getName()));
    }

    @GetMapping("/history")
    @Operation(summary = "Get workout history with pagination")
    public ResponseEntity<PagedResponse<WorkoutResponse>> getHistory(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(workoutService.getWorkoutHistory(authentication.getName(), page, size));
    }

    @GetMapping("/date/{date}")
    @Operation(summary = "Get workouts by date")
    public ResponseEntity<List<WorkoutResponse>> getByDate(Authentication authentication,
                                                            @PathVariable LocalDate date) {
        return ResponseEntity.ok(workoutService.getWorkoutsByDate(authentication.getName(), date));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a workout")
    public ResponseEntity<WorkoutResponse> updateWorkout(@PathVariable Long id,
                                                          Authentication authentication,
                                                          @Valid @RequestBody WorkoutRequest request) {
        return ResponseEntity.ok(workoutService.updateWorkout(id, authentication.getName(), request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a workout")
    public ResponseEntity<ApiResponse> deleteWorkout(@PathVariable Long id, Authentication authentication) {
        workoutService.deleteWorkout(id, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Workout deleted successfully"));
    }
}
