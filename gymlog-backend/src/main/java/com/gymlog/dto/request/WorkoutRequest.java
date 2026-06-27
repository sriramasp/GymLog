package com.gymlog.dto.request;

import com.gymlog.enums.WorkoutStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutRequest {

    @NotBlank(message = "Workout name is required")
    private String name;

    private String notes;

    @NotNull(message = "Workout date is required")
    private LocalDate workoutDate;

    private Integer durationMinutes;

    private WorkoutStatus status;

    @Valid
    private List<WorkoutExerciseRequest> exercises;
}
