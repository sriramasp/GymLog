package com.gymlog.controller;

import com.gymlog.dto.response.ExerciseCategoryResponse;
import com.gymlog.entity.ExerciseCategory;
import com.gymlog.service.impl.ExerciseServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exercise-categories")
@RequiredArgsConstructor
@Tag(name = "Exercise Categories", description = "Exercise category management")
public class ExerciseCategoryController {

    private final ExerciseServiceImpl exerciseService;

    @GetMapping
    @Operation(summary = "Get all exercise categories")
    public ResponseEntity<List<ExerciseCategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(exerciseService.getAllCategories());
    }

    @PostMapping
    @Operation(summary = "Create a new exercise category (Admin)")
    public ResponseEntity<ExerciseCategoryResponse> createCategory(@RequestBody ExerciseCategory category) {
        return new ResponseEntity<>(exerciseService.createCategory(category), HttpStatus.CREATED);
    }
}
