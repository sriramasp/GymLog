package com.gymlog.dto.response;

import com.gymlog.enums.WorkoutStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutResponse {
    private Long id;
    private String name;
    private String notes;
    private LocalDate workoutDate;
    private Integer durationMinutes;
    private WorkoutStatus status;
    private List<WorkoutExerciseResponse> exercises;
    private LocalDateTime createdAt;
    private int totalExercises;
    private int totalSets;
    private double totalVolume;
}
