package com.gymlog.repository;

import com.gymlog.entity.NutritionLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface NutritionLogRepository extends JpaRepository<NutritionLog, Long> {

    List<NutritionLog> findByUserIdAndLogDate(Long userId, LocalDate date);

    Page<NutritionLog> findByUserIdOrderByLogDateDesc(Long userId, Pageable pageable);

    List<NutritionLog> findByUserIdAndLogDateBetween(Long userId, LocalDate start, LocalDate end);

    @Query("SELECT COALESCE(SUM(n.calories), 0) FROM NutritionLog n WHERE n.user.id = :userId AND n.logDate = :date")
    Double sumCaloriesByDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    @Query("SELECT COALESCE(SUM(n.protein), 0) FROM NutritionLog n WHERE n.user.id = :userId AND n.logDate = :date")
    Double sumProteinByDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    @Query("SELECT COALESCE(SUM(n.carbs), 0) FROM NutritionLog n WHERE n.user.id = :userId AND n.logDate = :date")
    Double sumCarbsByDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    @Query("SELECT COALESCE(SUM(n.fat), 0) FROM NutritionLog n WHERE n.user.id = :userId AND n.logDate = :date")
    Double sumFatByDate(@Param("userId") Long userId, @Param("date") LocalDate date);

    long countByUserId(Long userId);
}
