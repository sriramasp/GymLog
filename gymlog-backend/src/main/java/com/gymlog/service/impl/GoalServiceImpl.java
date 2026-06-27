package com.gymlog.service.impl;

import com.gymlog.dto.request.GoalRequest;
import com.gymlog.dto.response.GoalResponse;
import com.gymlog.dto.response.PagedResponse;
import com.gymlog.entity.Goal;
import com.gymlog.entity.User;
import com.gymlog.enums.GoalStatus;
import com.gymlog.exception.ResourceNotFoundException;
import com.gymlog.repository.GoalRepository;
import com.gymlog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoalServiceImpl implements com.gymlog.service.GoalService {

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;

    @Transactional
    public GoalResponse createGoal(String email, GoalRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        Goal goal = Goal.builder()
                .user(user)
                .goalType(request.getGoalType())
                .title(request.getTitle())
                .description(request.getDescription())
                .targetValue(request.getTargetValue())
                .currentValue(request.getCurrentValue() != null ? request.getCurrentValue() : 0.0)
                .unit(request.getUnit())
                .startDate(request.getStartDate() != null ? request.getStartDate() : LocalDate.now())
                .targetDate(request.getTargetDate())
                .build();

        goal = goalRepository.save(goal);
        log.info("Goal created: {} for user: {}", goal.getTitle(), email);
        return mapToResponse(goal);
    }

    public PagedResponse<GoalResponse> getUserGoals(String email, int page, int size) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        Page<Goal> goalPage = goalRepository.findByUserIdOrderByCreatedAtDesc(
                user.getId(), PageRequest.of(page, size));

        List<GoalResponse> goals = goalPage.getContent().stream()
                .map(this::mapToResponse)
                .toList();

        return PagedResponse.<GoalResponse>builder()
                .content(goals)
                .page(goalPage.getNumber())
                .size(goalPage.getSize())
                .totalElements(goalPage.getTotalElements())
                .totalPages(goalPage.getTotalPages())
                .last(goalPage.isLast())
                .build();
    }

    public List<GoalResponse> getActiveGoals(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return goalRepository.findByUserIdAndStatus(user.getId(), GoalStatus.ACTIVE).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public GoalResponse updateGoal(Long id, GoalRequest request) {
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Goal", "id", id));

        goal.setGoalType(request.getGoalType());
        goal.setTitle(request.getTitle());
        goal.setDescription(request.getDescription());
        goal.setTargetValue(request.getTargetValue());
        if (request.getCurrentValue() != null) goal.setCurrentValue(request.getCurrentValue());
        goal.setUnit(request.getUnit());
        goal.setTargetDate(request.getTargetDate());

        goal = goalRepository.save(goal);
        return mapToResponse(goal);
    }

    @Transactional
    public GoalResponse updateProgress(Long id, Double currentValue) {
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Goal", "id", id));

        goal.setCurrentValue(currentValue);
        if (currentValue >= goal.getTargetValue()) {
            goal.setStatus(GoalStatus.COMPLETED);
        }

        goal = goalRepository.save(goal);
        log.info("Goal progress updated: {} -> {}", id, currentValue);
        return mapToResponse(goal);
    }

    @Transactional
    public void deleteGoal(Long id) {
        if (!goalRepository.existsById(id)) {
            throw new ResourceNotFoundException("Goal", "id", id);
        }
        goalRepository.deleteById(id);
    }

    private GoalResponse mapToResponse(Goal goal) {
        double progressPct = goal.getTargetValue() > 0
                ? Math.min((goal.getCurrentValue() / goal.getTargetValue()) * 100, 100)
                : 0;

        return GoalResponse.builder()
                .id(goal.getId())
                .goalType(goal.getGoalType())
                .title(goal.getTitle())
                .description(goal.getDescription())
                .targetValue(goal.getTargetValue())
                .currentValue(goal.getCurrentValue())
                .unit(goal.getUnit())
                .startDate(goal.getStartDate())
                .targetDate(goal.getTargetDate())
                .status(goal.getStatus())
                .progressPercentage(Math.round(progressPct * 10.0) / 10.0)
                .build();
    }
}
