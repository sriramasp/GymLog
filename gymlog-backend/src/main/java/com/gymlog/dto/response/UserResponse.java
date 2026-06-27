package com.gymlog.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
    private Double height;
    private Double currentWeight;
    private LocalDate dateOfBirth;
    private String gender;
    private Integer dailyCalorieTarget;
    private Boolean active;
    private LocalDateTime createdAt;
}
