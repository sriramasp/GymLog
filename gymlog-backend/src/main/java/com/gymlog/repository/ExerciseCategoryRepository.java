package com.gymlog.repository;

import com.gymlog.entity.ExerciseCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExerciseCategoryRepository extends JpaRepository<ExerciseCategory, Long> {
    Optional<ExerciseCategory> findByName(String name);
    boolean existsByName(String name);
}
