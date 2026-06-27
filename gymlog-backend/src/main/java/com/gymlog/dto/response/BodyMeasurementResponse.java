package com.gymlog.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BodyMeasurementResponse {
    private Long id;
    private Double weight;
    private Double bodyFatPct;
    private Double chest;
    private Double waist;
    private Double hips;
    private Double biceps;
    private Double thighs;
    private Double neck;
    private LocalDate measurementDate;
    private String notes;
}
