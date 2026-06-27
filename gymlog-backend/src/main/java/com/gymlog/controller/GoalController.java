package com.gymlog.controller;

import com.gymlog.dto.request.GoalRequest;
import com.gymlog.dto.response.ApiResponse;
import com.gymlog.dto.response.GoalResponse;
import com.gymlog.dto.response.PagedResponse;
import com.gymlog.service.GoalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
@Tag(name = "Goals", description = "Fitness goal management")
public class GoalController {

    private final GoalService goalService;

    @PostMapping
    @Operation(summary = "Create a new goal")
    public ResponseEntity<GoalResponse> createGoal(Authentication authentication,
                                                    @Valid @RequestBody GoalRequest request) {
        return new ResponseEntity<>(goalService.createGoal(authentication.getName(), request), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all goals with pagination")
    public ResponseEntity<PagedResponse<GoalResponse>> getGoals(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(goalService.getUserGoals(authentication.getName(), page, size));
    }

    @GetMapping("/active")
    @Operation(summary = "Get active goals")
    public ResponseEntity<List<GoalResponse>> getActiveGoals(Authentication authentication) {
        return ResponseEntity.ok(goalService.getActiveGoals(authentication.getName()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a goal")
    public ResponseEntity<GoalResponse> updateGoal(@PathVariable Long id,
                                                    @Valid @RequestBody GoalRequest request) {
        return ResponseEntity.ok(goalService.updateGoal(id, request));
    }

    @PutMapping("/{id}/progress")
    @Operation(summary = "Update goal progress")
    public ResponseEntity<GoalResponse> updateProgress(@PathVariable Long id,
                                                        @RequestParam Double value) {
        return ResponseEntity.ok(goalService.updateProgress(id, value));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a goal")
    public ResponseEntity<ApiResponse> deleteGoal(@PathVariable Long id) {
        goalService.deleteGoal(id);
        return ResponseEntity.ok(ApiResponse.success("Goal deleted successfully"));
    }
}
