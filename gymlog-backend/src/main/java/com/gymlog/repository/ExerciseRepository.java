package com.gymlog.repository;

import com.gymlog.entity.Exercise;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

    List<Exercise> findByCategoryId(Long categoryId);

    Page<Exercise> findByCategoryId(Long categoryId, Pageable pageable);

    @Query("SELECT e FROM Exercise e WHERE " +
           "LOWER(e.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(e.muscleGroup) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(e.equipment) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Exercise> search(@Param("query") String query, Pageable pageable);

    @Query("SELECT e FROM Exercise e WHERE e.isCustom = false OR e.createdBy.id = :userId")
    Page<Exercise> findAllAccessible(@Param("userId") Long userId, Pageable pageable);

    List<Exercise> findByMuscleGroup(String muscleGroup);

    Optional<Exercise> findByName(String name);
}
