package com.gymlog.controller;

import com.gymlog.dto.request.BodyMeasurementRequest;
import com.gymlog.dto.response.ApiResponse;
import com.gymlog.dto.response.BodyMeasurementResponse;
import com.gymlog.dto.response.PagedResponse;
import com.gymlog.service.impl.BodyMeasurementServiceImpl;
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
@RequestMapping("/api/body-measurements")
@RequiredArgsConstructor
@Tag(name = "Body Measurements", description = "Body progress tracking")
public class BodyMeasurementController {

    private final BodyMeasurementServiceImpl measurementService;

    @PostMapping
    @Operation(summary = "Record a body measurement")
    public ResponseEntity<BodyMeasurementResponse> createMeasurement(Authentication authentication,
                                                                      @Valid @RequestBody BodyMeasurementRequest request) {
        return new ResponseEntity<>(measurementService.createMeasurement(authentication.getName(), request), HttpStatus.CREATED);
    }

    @GetMapping("/history")
    @Operation(summary = "Get measurement history with pagination")
    public ResponseEntity<PagedResponse<BodyMeasurementResponse>> getHistory(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(measurementService.getMeasurementHistory(authentication.getName(), page, size));
    }

    @GetMapping("/all")
    @Operation(summary = "Get all measurements (for charts)")
    public ResponseEntity<List<BodyMeasurementResponse>> getAll(Authentication authentication) {
        return ResponseEntity.ok(measurementService.getAllMeasurements(authentication.getName()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a body measurement")
    public ResponseEntity<ApiResponse> deleteMeasurement(@PathVariable Long id) {
        measurementService.deleteMeasurement(id);
        return ResponseEntity.ok(ApiResponse.success("Measurement deleted successfully"));
    }
}
