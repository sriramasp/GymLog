package com.gymlog.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonalRecordResponse {
    private Long id;
    private Long exerciseId;
    private String exerciseName;
    private Double maxWeight;
    private Integer maxReps;
    private Double maxVolume;
    private LocalDate achievedDate;
}
