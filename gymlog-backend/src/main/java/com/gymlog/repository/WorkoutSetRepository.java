package com.gymlog.repository;

import com.gymlog.entity.WorkoutSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkoutSetRepository extends JpaRepository<WorkoutSet, Long> {

    List<WorkoutSet> findByWorkoutExerciseIdOrderBySetNumberAsc(Long workoutExerciseId);

    @Query("SELECT ws FROM WorkoutSet ws WHERE ws.workoutExercise.workout.user.id = :userId " +
           "AND ws.workoutExercise.exercise.id = :exerciseId ORDER BY ws.weight DESC")
    List<WorkoutSet> findTopSetsByUserAndExercise(@Param("userId") Long userId, @Param("exerciseId") Long exerciseId);
}
