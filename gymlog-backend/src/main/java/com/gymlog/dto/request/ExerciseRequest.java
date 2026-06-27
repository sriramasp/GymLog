package com.gymlog.dto.request;

import com.gymlog.enums.Difficulty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseRequest {

    @NotBlank(message = "Exercise name is required")
    private String name;

    private String description;
    private String instructions;
    private Difficulty difficulty;
    private String muscleGroup;
    private String equipment;
    private String imageUrl;
    private Long categoryId;
}
