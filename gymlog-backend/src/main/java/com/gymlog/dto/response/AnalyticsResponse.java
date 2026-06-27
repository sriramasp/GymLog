package com.gymlog.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsResponse {
    private List<DataPoint> weightProgression;
    private List<DataPoint> strengthProgression;
    private List<DataPoint> volumeTrends;
    private List<DataPoint> calorieTrends;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataPoint {
        private LocalDate date;
        private Double value;
        private String label;
    }
}
