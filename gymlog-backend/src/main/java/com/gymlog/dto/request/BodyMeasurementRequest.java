package com.gymlog.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BodyMeasurementRequest {

    private Double weight;
    private Double bodyFatPct;
    private Double chest;
    private Double waist;
    private Double hips;
    private Double biceps;
    private Double thighs;
    private Double neck;
    private String notes;

    @NotNull(message = "Measurement date is required")
    private LocalDate measurementDate;
}
