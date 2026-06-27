package com.gymlog.controller;

import com.gymlog.dto.request.ExerciseRequest;
import com.gymlog.dto.response.ApiResponse;
import com.gymlog.dto.response.ExerciseCategoryResponse;
import com.gymlog.dto.response.ExerciseResponse;
import com.gymlog.dto.response.PagedResponse;
import com.gymlog.entity.ExerciseCategory;
import com.gymlog.service.impl.ExerciseServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exercises")
@RequiredArgsConstructor
@Tag(name = "Exercises", description = "Exercise library management")
public class ExerciseController {

    private final ExerciseServiceImpl exerciseService;

    @GetMapping
    @Operation(summary = "Get all exercises with pagination")
    public ResponseEntity<PagedResponse<ExerciseResponse>> getAllExercises(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(exerciseService.getAllExercises(page, size));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get exercise by ID")
    public ResponseEntity<ExerciseResponse> getExercise(@PathVariable Long id) {
        return ResponseEntity.ok(exerciseService.getExerciseById(id));
    }

    @GetMapping("/search")
    @Operation(summary = "Search exercises by name, muscle group, or equipment")
    public ResponseEntity<PagedResponse<ExerciseResponse>> searchExercises(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(exerciseService.searchExercises(query, page, size));
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get exercises by category")
    public ResponseEntity<PagedResponse<ExerciseResponse>> getByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(exerciseService.getExercisesByCategory(categoryId, page, size));
    }

    @PostMapping
    @Operation(summary = "Create a new exercise (Admin)")
    public ResponseEntity<ExerciseResponse> createExercise(@Valid @RequestBody ExerciseRequest request) {
        return new ResponseEntity<>(exerciseService.createExercise(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an exercise (Admin)")
    public ResponseEntity<ExerciseResponse> updateExercise(@PathVariable Long id,
                                                            @Valid @RequestBody ExerciseRequest request) {
        return ResponseEntity.ok(exerciseService.updateExercise(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an exercise (Admin)")
    public ResponseEntity<ApiResponse> deleteExercise(@PathVariable Long id) {
        exerciseService.deleteExercise(id);
        return ResponseEntity.ok(ApiResponse.success("Exercise deleted successfully"));
    }
}
