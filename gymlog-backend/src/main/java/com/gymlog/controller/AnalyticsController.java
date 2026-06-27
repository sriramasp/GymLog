package com.gymlog.controller;

import com.gymlog.dto.response.AnalyticsResponse.DataPoint;
import com.gymlog.service.impl.AnalyticsServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics", description = "Progress analytics and charts")
public class AnalyticsController {

    private final AnalyticsServiceImpl analyticsService;

    @GetMapping("/weight")
    @Operation(summary = "Get weight progression data")
    public ResponseEntity<List<DataPoint>> getWeightProgression(Authentication authentication) {
        return ResponseEntity.ok(analyticsService.getWeightProgression(authentication.getName()));
    }

    @GetMapping("/strength/{exerciseId}")
    @Operation(summary = "Get strength progression for a specific exercise")
    public ResponseEntity<List<DataPoint>> getStrengthProgression(Authentication authentication,
                                                                    @PathVariable Long exerciseId) {
        return ResponseEntity.ok(analyticsService.getStrengthProgression(authentication.getName(), exerciseId));
    }

    @GetMapping("/volume")
    @Operation(summary = "Get workout volume trends")
    public ResponseEntity<List<DataPoint>> getVolumeTrends(Authentication authentication,
                                                            @RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(analyticsService.getVolumeTrends(authentication.getName(), days));
    }

    @GetMapping("/calories")
    @Operation(summary = "Get calorie intake trends")
    public ResponseEntity<List<DataPoint>> getCalorieTrends(Authentication authentication,
                                                             @RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(analyticsService.getCalorieTrends(authentication.getName(), days));
    }
}
