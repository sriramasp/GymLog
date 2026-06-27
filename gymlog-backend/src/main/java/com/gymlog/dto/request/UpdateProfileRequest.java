package com.gymlog.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {
    private String firstName;
    private String lastName;
    private Double height;
    private Double currentWeight;
    private LocalDate dateOfBirth;
    private String gender;
    private Integer dailyCalorieTarget;
}
