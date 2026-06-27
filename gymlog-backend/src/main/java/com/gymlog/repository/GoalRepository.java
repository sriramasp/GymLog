package com.gymlog.repository;

import com.gymlog.entity.Goal;
import com.gymlog.enums.GoalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {

    Page<Goal> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    List<Goal> findByUserIdAndStatus(Long userId, GoalStatus status);

    long countByUserIdAndStatus(Long userId, GoalStatus status);
}
