package com.gymlog.service.impl;

import com.gymlog.dto.request.NutritionLogRequest;
import com.gymlog.dto.response.DailyNutritionSummary;
import com.gymlog.dto.response.NutritionLogResponse;
import com.gymlog.dto.response.PagedResponse;
import com.gymlog.entity.NutritionLog;
import com.gymlog.entity.User;
import com.gymlog.exception.ResourceNotFoundException;
import com.gymlog.repository.NutritionLogRepository;
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
public class NutritionServiceImpl {

    private final NutritionLogRepository nutritionLogRepository;
    private final UserRepository userRepository;

    @Transactional
    public NutritionLogResponse createLog(String email, NutritionLogRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        NutritionLog log1 = NutritionLog.builder()
                .user(user)
                .foodName(request.getFoodName())
                .calories(request.getCalories())
                .protein(request.getProtein())
                .carbs(request.getCarbs())
                .fat(request.getFat())
                .servingSize(request.getServingSize())
                .servingUnit(request.getServingUnit())
                .mealType(request.getMealType())
                .logDate(request.getLogDate())
                .build();

        log1 = nutritionLogRepository.save(log1);
        log.info("Nutrition log created for user: {}", email);
        return mapToResponse(log1);
    }

    public List<NutritionLogResponse> getLogsByDate(String email, LocalDate date) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return nutritionLogRepository.findByUserIdAndLogDate(user.getId(), date).stream()
                .map(this::mapToResponse)
                .toList();
    }

    public PagedResponse<NutritionLogResponse> getLogHistory(String email, int page, int size) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        Page<NutritionLog> logPage = nutritionLogRepository.findByUserIdOrderByLogDateDesc(
                user.getId(), PageRequest.of(page, size));

        List<NutritionLogResponse> logs = logPage.getContent().stream()
                .map(this::mapToResponse)
                .toList();

        return PagedResponse.<NutritionLogResponse>builder()
                .content(logs)
                .page(logPage.getNumber())
                .size(logPage.getSize())
                .totalElements(logPage.getTotalElements())
                .totalPages(logPage.getTotalPages())
                .last(logPage.isLast())
                .build();
    }

    public DailyNutritionSummary getDailySummary(String email, LocalDate date) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        Double totalCalories = nutritionLogRepository.sumCaloriesByDate(user.getId(), date);
        Double totalProtein = nutritionLogRepository.sumProteinByDate(user.getId(), date);
        Double totalCarbs = nutritionLogRepository.sumCarbsByDate(user.getId(), date);
        Double totalFat = nutritionLogRepository.sumFatByDate(user.getId(), date);

        int calorieTarget = user.getDailyCalorieTarget() != null ? user.getDailyCalorieTarget() : 2000;
        double pct = calorieTarget > 0 ? (totalCalories / calorieTarget) * 100 : 0;

        return DailyNutritionSummary.builder()
                .date(date)
                .totalCalories(totalCalories)
                .totalProtein(totalProtein)
                .totalCarbs(totalCarbs)
                .totalFat(totalFat)
                .calorieTarget(calorieTarget)
                .caloriePercentage(Math.round(pct * 10.0) / 10.0)
                .build();
    }

    @Transactional
    public NutritionLogResponse updateLog(Long id, NutritionLogRequest request) {
        NutritionLog nutritionLog = nutritionLogRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("NutritionLog", "id", id));

        nutritionLog.setFoodName(request.getFoodName());
        nutritionLog.setCalories(request.getCalories());
        nutritionLog.setProtein(request.getProtein());
        nutritionLog.setCarbs(request.getCarbs());
        nutritionLog.setFat(request.getFat());
        nutritionLog.setServingSize(request.getServingSize());
        nutritionLog.setServingUnit(request.getServingUnit());
        nutritionLog.setMealType(request.getMealType());
        nutritionLog.setLogDate(request.getLogDate());

        nutritionLog = nutritionLogRepository.save(nutritionLog);
        return mapToResponse(nutritionLog);
    }

    @Transactional
    public void deleteLog(Long id) {
        if (!nutritionLogRepository.existsById(id)) {
            throw new ResourceNotFoundException("NutritionLog", "id", id);
        }
        nutritionLogRepository.deleteById(id);
    }

    private NutritionLogResponse mapToResponse(NutritionLog log) {
        return NutritionLogResponse.builder()
                .id(log.getId())
                .foodName(log.getFoodName())
                .calories(log.getCalories())
                .protein(log.getProtein())
                .carbs(log.getCarbs())
                .fat(log.getFat())
                .servingSize(log.getServingSize())
                .servingUnit(log.getServingUnit())
                .mealType(log.getMealType())
                .logDate(log.getLogDate())
                .build();
    }
}
