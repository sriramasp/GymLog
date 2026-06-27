package com.gymlog.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutExerciseResponse {
    private Long id;
    private Long exerciseId;
    private String exerciseName;
    private String muscleGroup;
    private Integer orderIndex;
    private String notes;
    private List<WorkoutSetResponse> sets;
}
