package com.gymlog.repository;

import com.gymlog.entity.BodyMeasurement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BodyMeasurementRepository extends JpaRepository<BodyMeasurement, Long> {

    Page<BodyMeasurement> findByUserIdOrderByMeasurementDateDesc(Long userId, Pageable pageable);

    List<BodyMeasurement> findByUserIdAndMeasurementDateBetween(Long userId, LocalDate start, LocalDate end);

    Optional<BodyMeasurement> findTopByUserIdOrderByMeasurementDateDesc(Long userId);

    List<BodyMeasurement> findByUserIdOrderByMeasurementDateAsc(Long userId);
}
