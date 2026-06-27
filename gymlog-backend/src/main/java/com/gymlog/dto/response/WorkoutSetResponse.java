package com.gymlog.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutSetResponse {
    private Long id;
    private Integer setNumber;
    private Integer reps;
    private Double weight;
    private Integer durationSeconds;
    private Boolean isWarmup;
    private Boolean completed;
}
