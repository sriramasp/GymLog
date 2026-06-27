package com.gymlog.repository;

import com.gymlog.entity.PersonalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonalRecordRepository extends JpaRepository<PersonalRecord, Long> {

    List<PersonalRecord> findByUserIdOrderByAchievedDateDesc(Long userId);

    Optional<PersonalRecord> findByUserIdAndExerciseId(Long userId, Long exerciseId);

    List<PersonalRecord> findByUserId(Long userId);
}
