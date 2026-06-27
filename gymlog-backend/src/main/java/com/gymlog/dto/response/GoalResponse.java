package com.gymlog.dto.response;

import com.gymlog.enums.GoalStatus;
import com.gymlog.enums.GoalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalResponse {
    private Long id;
    private GoalType goalType;
    private String title;
    private String description;
    private Double targetValue;
    private Double currentValue;
    private String unit;
    private LocalDate startDate;
    private LocalDate targetDate;
    private GoalStatus status;
    private Double progressPercentage;
}
