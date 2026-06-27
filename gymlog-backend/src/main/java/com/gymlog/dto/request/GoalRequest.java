package com.gymlog.dto.request;

import com.gymlog.enums.GoalType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoalRequest {

    @NotNull(message = "Goal type is required")
    private GoalType goalType;

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Target value is required")
    private Double targetValue;

    private Double currentValue;
    private String unit;
    private LocalDate startDate;
    private LocalDate targetDate;
}
