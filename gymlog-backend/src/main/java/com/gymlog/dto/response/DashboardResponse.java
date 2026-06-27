package com.gymlog.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {
    // Today's workout summary
    private int todayWorkouts;
    private int todayExercises;
    private int todaySets;
    private double todayVolume;

    // Nutrition summary
    private double todayCalories;
    private int calorieTarget;
    private double caloriePercentage;
    private double todayProtein;
    private double todayCarbs;
    private double todayFat;

    // Streak
    private int workoutStreak;

    // Weekly stats
    private int weeklyWorkouts;
    private double weeklyVolume;
    private double weeklyCaloriesAvg;

    // Goals
    private int activeGoals;
    private int completedGoals;
    private List<GoalResponse> recentGoals;

    // Recent PRs
    private List<PersonalRecordResponse> recentPRs;
}
