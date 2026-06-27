package com.gymlog.dto.response;

import com.gymlog.enums.Difficulty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseResponse {
    private Long id;
    private String name;
    private String description;
    private String instructions;
    private Difficulty difficulty;
    private String muscleGroup;
    private String equipment;
    private String imageUrl;
    private Long categoryId;
    private String categoryName;
    private Boolean isCustom;
}
