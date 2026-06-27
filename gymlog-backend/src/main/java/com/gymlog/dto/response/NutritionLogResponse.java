package com.gymlog.dto.response;

import com.gymlog.enums.MealType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NutritionLogResponse {
    private Long id;
    private String foodName;
    private Double calories;
    private Double protein;
    private Double carbs;
    private Double fat;
    private Double servingSize;
    private String servingUnit;
    private MealType mealType;
    private LocalDate logDate;
}
