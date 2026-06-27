package com.gymlog.service.impl;

import com.gymlog.dto.response.*;
import com.gymlog.entity.User;
import com.gymlog.entity.Workout;
import com.gymlog.enums.GoalStatus;
import com.gymlog.enums.WorkoutStatus;
import com.gymlog.exception.ResourceNotFoundException;
import com.gymlog.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl {

    private final UserRepository userRepository;
    private final WorkoutRepository workoutRepository;
    private final NutritionLogRepository nutritionLogRepository;
    private final GoalRepository goalRepository;
    private final PersonalRecordRepository personalRecordRepository;
    private final GoalServiceImpl goalService;

    public DashboardResponse getDashboard(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.minusDays(6);

        // Today's workout summary
        var todayWorkouts = workoutRepository.findByUserIdAndWorkoutDate(user.getId(), today);
        int todayWorkoutCount = todayWorkouts.size();
        int todayExercises = todayWorkouts.stream()
                .mapToInt(w -> w.getExercises().size())
                .sum();
        int todaySets = todayWorkouts.stream()
                .flatMap(w -> w.getExercises().stream())
                .mapToInt(we -> we.getSets().size())
                .sum();
        double todayVolume = todayWorkouts.stream()
                .flatMap(w -> w.getExercises().stream())
                .flatMap(we -> we.getSets().stream())
                .filter(s -> s.getWeight() != null && s.getReps() != null)
                .mapToDouble(s -> s.getWeight() * s.getReps())
                .sum();

        // Nutrition summary
        Double todayCalories = nutritionLogRepository.sumCaloriesByDate(user.getId(), today);
        Double todayProtein = nutritionLogRepository.sumProteinByDate(user.getId(), today);
        Double todayCarbs = nutritionLogRepository.sumCarbsByDate(user.getId(), today);
        Double todayFat = nutritionLogRepository.sumFatByDate(user.getId(), today);
        int calorieTarget = user.getDailyCalorieTarget() != null ? user.getDailyCalorieTarget() : 2000;
        double caloriePct = calorieTarget > 0 ? (todayCalories / calorieTarget) * 100 : 0;

        // Workout streak
        int streak = calculateWorkoutStreak(user.getId());

        // Weekly stats
        long weeklyWorkouts = workoutRepository.countCompletedInRange(user.getId(), weekStart, today);
        var weeklyWorkoutList = workoutRepository.findByUserIdAndWorkoutDateBetween(user.getId(), weekStart, today);
        double weeklyVolume = weeklyWorkoutList.stream()
                .flatMap(w -> w.getExercises().stream())
                .flatMap(we -> we.getSets().stream())
                .filter(s -> s.getWeight() != null && s.getReps() != null)
                .mapToDouble(s -> s.getWeight() * s.getReps())
                .sum();

        var weeklyNutritionLogs = nutritionLogRepository.findByUserIdAndLogDateBetween(user.getId(), weekStart, today);
        double weeklyCaloriesAvg = weeklyNutritionLogs.isEmpty() ? 0 :
                weeklyNutritionLogs.stream().mapToDouble(n -> n.getCalories()).sum() / 7.0;

        // Goals
        int activeGoals = (int) goalRepository.countByUserIdAndStatus(user.getId(), GoalStatus.ACTIVE);
        int completedGoals = (int) goalRepository.countByUserIdAndStatus(user.getId(), GoalStatus.COMPLETED);
        List<GoalResponse> recentGoals = goalService.getActiveGoals(email);
        if (recentGoals.size() > 3) recentGoals = recentGoals.subList(0, 3);

        List<WorkoutResponse> recentWorkouts = workoutRepository.findByUserIdAndWorkoutDateBetween(user.getId(), weekStart, today).stream()
                .sorted(Comparator.comparing(Workout::getWorkoutDate).reversed())
                .limit(6)
                .map(this::mapToResponse)
                .toList();

        // Recent PRs
        List<PersonalRecordResponse> recentPRs = personalRecordRepository.findByUserIdOrderByAchievedDateDesc(user.getId())
                .stream()
                .limit(5)
                .map(pr -> PersonalRecordResponse.builder()
                        .id(pr.getId())
                        .exerciseId(pr.getExercise().getId())
                        .exerciseName(pr.getExercise().getName())
                        .maxWeight(pr.getMaxWeight())
                        .maxReps(pr.getMaxReps())
                        .maxVolume(pr.getMaxVolume())
                        .achievedDate(pr.getAchievedDate())
                        .build())
                .toList();

        return DashboardResponse.builder()
                .todayWorkouts(todayWorkoutCount)
                .todayExercises(todayExercises)
                .todaySets(todaySets)
                .todayVolume(todayVolume)
                .todayCalories(todayCalories)
                .calorieTarget(calorieTarget)
                .caloriePercentage(Math.round(caloriePct * 10.0) / 10.0)
                .todayProtein(todayProtein)
                .todayCarbs(todayCarbs)
                .todayFat(todayFat)
                .workoutStreak(streak)
                .weeklyWorkouts((int) weeklyWorkouts)
                .weeklyVolume(weeklyVolume)
                .weeklyCaloriesAvg(Math.round(weeklyCaloriesAvg * 10.0) / 10.0)
                .activeGoals(activeGoals)
                .completedGoals(completedGoals)
                .recentGoals(recentGoals)
                .recentWorkouts(recentWorkouts)
                .recentPRs(recentPRs)
                .build();
    }

    private WorkoutResponse mapToResponse(Workout workout) {
        List<WorkoutExerciseResponse> exerciseResponses = workout.getExercises().stream()
                .map(we -> WorkoutExerciseResponse.builder()
                        .id(we.getId())
                        .exerciseId(we.getExercise().getId())
                        .exerciseName(we.getExercise().getName())
                        .muscleGroup(we.getExercise().getMuscleGroup())
                        .orderIndex(we.getOrderIndex())
                        .notes(we.getNotes())
                        .sets(we.getSets().stream()
                                .map(s -> WorkoutSetResponse.builder()
                                        .id(s.getId())
                                        .setNumber(s.getSetNumber())
                                        .reps(s.getReps())
                                        .weight(s.getWeight())
                                        .durationSeconds(s.getDurationSeconds())
                                        .isWarmup(s.getIsWarmup())
                                        .completed(s.getCompleted())
                                        .build())
                                .toList())
                        .build())
                .toList();

        int totalSets = exerciseResponses.stream()
                .mapToInt(e -> e.getSets() != null ? e.getSets().size() : 0)
                .sum();

        double totalVolume = workout.getExercises().stream()
                .flatMap(we -> we.getSets().stream())
                .filter(s -> s.getWeight() != null && s.getReps() != null)
                .mapToDouble(s -> s.getWeight() * s.getReps())
                .sum();

        return WorkoutResponse.builder()
                .id(workout.getId())
                .name(workout.getName())
                .notes(workout.getNotes())
                .workoutDate(workout.getWorkoutDate())
                .durationMinutes(workout.getDurationMinutes())
                .status(workout.getStatus())
                .exercises(exerciseResponses)
                .createdAt(workout.getCreatedAt())
                .totalExercises(exerciseResponses.size())
                .totalSets(totalSets)
                .totalVolume(totalVolume)
                .build();
    }

    private int calculateWorkoutStreak(Long userId) {
        List<LocalDate> dates = workoutRepository.findCompletedWorkoutDates(userId);
        if (dates.isEmpty()) return 0;

        int streak = 0;
        LocalDate checkDate = LocalDate.now();

        for (LocalDate date : dates) {
            if (date.equals(checkDate) || date.equals(checkDate.minusDays(1))) {
                streak++;
                checkDate = date.minusDays(1);
            } else {
                break;
            }
        }
        return streak;
    }
}
