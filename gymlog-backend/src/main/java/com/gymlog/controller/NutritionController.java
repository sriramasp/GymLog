package com.gymlog.controller;

import com.gymlog.dto.request.NutritionLogRequest;
import com.gymlog.dto.response.*;
import com.gymlog.service.impl.NutritionServiceImpl;
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
@RequestMapping("/api/nutrition")
@RequiredArgsConstructor
@Tag(name = "Nutrition", description = "Nutrition and calorie tracking")
public class NutritionController {

    private final NutritionServiceImpl nutritionService;

    @PostMapping
    @Operation(summary = "Log a food entry")
    public ResponseEntity<NutritionLogResponse> createLog(Authentication authentication,
                                                           @Valid @RequestBody NutritionLogRequest request) {
        return new ResponseEntity<>(nutritionService.createLog(authentication.getName(), request), HttpStatus.CREATED);
    }

    @GetMapping("/date/{date}")
    @Operation(summary = "Get nutrition logs for a specific date")
    public ResponseEntity<List<NutritionLogResponse>> getByDate(Authentication authentication,
                                                                  @PathVariable LocalDate date) {
        return ResponseEntity.ok(nutritionService.getLogsByDate(authentication.getName(), date));
    }

    @GetMapping("/daily-summary/{date}")
    @Operation(summary = "Get daily nutrition summary with macro totals")
    public ResponseEntity<DailyNutritionSummary> getDailySummary(Authentication authentication,
                                                                  @PathVariable LocalDate date) {
        return ResponseEntity.ok(nutritionService.getDailySummary(authentication.getName(), date));
    }

    @GetMapping("/history")
    @Operation(summary = "Get nutrition log history with pagination")
    public ResponseEntity<PagedResponse<NutritionLogResponse>> getHistory(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(nutritionService.getLogHistory(authentication.getName(), page, size));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a nutrition log entry")
    public ResponseEntity<NutritionLogResponse> updateLog(@PathVariable Long id,
                                                           @Valid @RequestBody NutritionLogRequest request) {
        return ResponseEntity.ok(nutritionService.updateLog(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a nutrition log entry")
    public ResponseEntity<ApiResponse> deleteLog(@PathVariable Long id) {
        nutritionService.deleteLog(id);
        return ResponseEntity.ok(ApiResponse.success("Nutrition log deleted successfully"));
    }
}
