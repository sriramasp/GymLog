package com.gymlog.service.impl;

import com.gymlog.dto.request.BodyMeasurementRequest;
import com.gymlog.dto.response.BodyMeasurementResponse;
import com.gymlog.dto.response.PagedResponse;
import com.gymlog.entity.BodyMeasurement;
import com.gymlog.entity.User;
import com.gymlog.exception.ResourceNotFoundException;
import com.gymlog.repository.BodyMeasurementRepository;
import com.gymlog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BodyMeasurementServiceImpl {

    private final BodyMeasurementRepository measurementRepository;
    private final UserRepository userRepository;

    @Transactional
    public BodyMeasurementResponse createMeasurement(String email, BodyMeasurementRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        BodyMeasurement measurement = BodyMeasurement.builder()
                .user(user)
                .weight(request.getWeight())
                .bodyFatPct(request.getBodyFatPct())
                .chest(request.getChest())
                .waist(request.getWaist())
                .hips(request.getHips())
                .biceps(request.getBiceps())
                .thighs(request.getThighs())
                .neck(request.getNeck())
                .measurementDate(request.getMeasurementDate())
                .notes(request.getNotes())
                .build();

        measurement = measurementRepository.save(measurement);

        // Update user's current weight if weight is provided
        if (request.getWeight() != null) {
            user.setCurrentWeight(request.getWeight());
            userRepository.save(user);
        }

        log.info("Body measurement recorded for user: {}", email);
        return mapToResponse(measurement);
    }

    public PagedResponse<BodyMeasurementResponse> getMeasurementHistory(String email, int page, int size) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        Page<BodyMeasurement> measurementPage = measurementRepository.findByUserIdOrderByMeasurementDateDesc(
                user.getId(), PageRequest.of(page, size));

        List<BodyMeasurementResponse> measurements = measurementPage.getContent().stream()
                .map(this::mapToResponse)
                .toList();

        return PagedResponse.<BodyMeasurementResponse>builder()
                .content(measurements)
                .page(measurementPage.getNumber())
                .size(measurementPage.getSize())
                .totalElements(measurementPage.getTotalElements())
                .totalPages(measurementPage.getTotalPages())
                .last(measurementPage.isLast())
                .build();
    }

    public List<BodyMeasurementResponse> getAllMeasurements(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return measurementRepository.findByUserIdOrderByMeasurementDateAsc(user.getId()).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public void deleteMeasurement(Long id) {
        if (!measurementRepository.existsById(id)) {
            throw new ResourceNotFoundException("BodyMeasurement", "id", id);
        }
        measurementRepository.deleteById(id);
    }

    private BodyMeasurementResponse mapToResponse(BodyMeasurement m) {
        return BodyMeasurementResponse.builder()
                .id(m.getId())
                .weight(m.getWeight())
                .bodyFatPct(m.getBodyFatPct())
                .chest(m.getChest())
                .waist(m.getWaist())
                .hips(m.getHips())
                .biceps(m.getBiceps())
                .thighs(m.getThighs())
                .neck(m.getNeck())
                .measurementDate(m.getMeasurementDate())
                .notes(m.getNotes())
                .build();
    }
}
