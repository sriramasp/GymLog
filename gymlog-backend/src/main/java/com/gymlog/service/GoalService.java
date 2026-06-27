package com.gymlog.service;

import com.gymlog.dto.request.GoalRequest;
import com.gymlog.dto.response.GoalResponse;
import com.gymlog.dto.response.PagedResponse;

import java.util.List;

public interface GoalService {
    GoalResponse createGoal(String email, GoalRequest request);
    PagedResponse<GoalResponse> getUserGoals(String email, int page, int size);
    List<GoalResponse> getActiveGoals(String email);
    GoalResponse updateGoal(Long id, GoalRequest request);
    GoalResponse updateProgress(Long id, Double currentValue);
    void deleteGoal(Long id);
}
