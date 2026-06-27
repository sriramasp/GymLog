package com.gymlog.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutSetRequest {
    private Integer setNumber;
    private Integer reps;
    private Double weight;
    private Integer durationSeconds;
    private Boolean isWarmup;
    private Boolean completed;
}
