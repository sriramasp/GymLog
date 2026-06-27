package com.gymlog.service.impl;

import com.gymlog.dto.response.AnalyticsResponse;
import com.gymlog.dto.response.AnalyticsResponse.DataPoint;
import com.gymlog.entity.BodyMeasurement;
import com.gymlog.entity.User;
import com.gymlog.exception.ResourceNotFoundException;
import com.gymlog.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl {

    private final UserRepository userRepository;
    private final BodyMeasurementRepository bodyMeasurementRepository;
    private final WorkoutRepository workoutRepository;
    private final NutritionLogRepository nutritionLogRepository;
    private final WorkoutSetRepository workoutSetRepository;

    public List<DataPoint> getWeightProgression(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        return bodyMeasurementRepository.findByUserIdOrderByMeasurementDateAsc(user.getId()).stream()
                .filter(m -> m.getWeight() != null)
                .map(m -> DataPoint.builder()
                        .date(m.getMeasurementDate())
                        .value(m.getWeight())
                        .label("Weight (kg)")
                        .build())
                .toList();
    }

    public List<DataPoint> getStrengthProgression(String email, Long exerciseId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        var sets = workoutSetRepository.findTopSetsByUserAndExercise(user.getId(), exerciseId);
        // Group by date and get max weight per date
        var dateMaxMap = new java.util.LinkedHashMap<LocalDate, Double>();
        sets.forEach(s -> {
            LocalDate date = s.getWorkoutExercise().getWorkout().getWorkoutDate();
            dateMaxMap.merge(date, s.getWeight() != null ? s.getWeight() : 0.0, Math::max);
        });

        return dateMaxMap.entrySet().stream()
                .sorted(java.util.Map.Entry.comparingByKey())
                .map(e -> DataPoint.builder()
                        .date(e.getKey())
                        .value(e.getValue())
                        .label("Max Weight (kg)")
                        .build())
                .toList();
    }

    public List<DataPoint> getVolumeTrends(String email, int days) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(days);

        var workouts = workoutRepository.findByUserIdAndWorkoutDateBetween(user.getId(), start, end);
        var dateVolumeMap = new java.util.LinkedHashMap<LocalDate, Double>();

        workouts.forEach(w -> {
            double volume = w.getExercises().stream()
                    .flatMap(we -> we.getSets().stream())
                    .filter(s -> s.getWeight() != null && s.getReps() != null)
                    .mapToDouble(s -> s.getWeight() * s.getReps())
                    .sum();
            dateVolumeMap.merge(w.getWorkoutDate(), volume, Double::sum);
        });

        return dateVolumeMap.entrySet().stream()
                .sorted(java.util.Map.Entry.comparingByKey())
                .map(e -> DataPoint.builder()
                        .date(e.getKey())
                        .value(e.getValue())
                        .label("Volume (kg×reps)")
                        .build())
                .toList();
    }

    public List<DataPoint> getCalorieTrends(String email, int days) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(days);

        List<DataPoint> points = new ArrayList<>();
        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            Double calories = nutritionLogRepository.sumCaloriesByDate(user.getId(), date);
            if (calories > 0) {
                points.add(DataPoint.builder()
                        .date(date)
                        .value(calories)
                        .label("Calories")
                        .build());
            }
        }
        return points;
    }
}
