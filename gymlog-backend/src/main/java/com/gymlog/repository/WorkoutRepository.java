package com.gymlog.repository;

import com.gymlog.entity.Workout;
import com.gymlog.enums.WorkoutStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface WorkoutRepository extends JpaRepository<Workout, Long> {

    Page<Workout> findByUserIdOrderByWorkoutDateDesc(Long userId, Pageable pageable);

    List<Workout> findByUserIdAndWorkoutDate(Long userId, LocalDate date);

    List<Workout> findByUserIdAndWorkoutDateBetween(Long userId, LocalDate start, LocalDate end);

    @Query("SELECT COUNT(w) FROM Workout w WHERE w.user.id = :userId AND w.status = :status")
    long countByUserIdAndStatus(@Param("userId") Long userId, @Param("status") WorkoutStatus status);

    @Query("SELECT COUNT(w) FROM Workout w WHERE w.user.id = :userId AND w.status = 'COMPLETED' AND w.workoutDate BETWEEN :start AND :end")
    long countCompletedInRange(@Param("userId") Long userId, @Param("start") LocalDate start, @Param("end") LocalDate end);

    @Query("SELECT DISTINCT w.workoutDate FROM Workout w WHERE w.user.id = :userId AND w.status = 'COMPLETED' ORDER BY w.workoutDate DESC")
    List<LocalDate> findCompletedWorkoutDates(@Param("userId") Long userId);

    long countByUserId(Long userId);
}
