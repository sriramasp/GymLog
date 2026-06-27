package com.gymlog.dto.request;

import com.gymlog.enums.MealType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NutritionLogRequest {

    @NotBlank(message = "Food name is required")
    private String foodName;

    @NotNull(message = "Calories is required")
    @Positive(message = "Calories must be positive")
    private Double calories;

    private Double protein;
    private Double carbs;
    private Double fat;
    private Double servingSize;
    private String servingUnit;
    private MealType mealType;

    @NotNull(message = "Log date is required")
    private LocalDate logDate;
}
