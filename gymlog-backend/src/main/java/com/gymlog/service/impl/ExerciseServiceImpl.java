package com.gymlog.service.impl;

import com.gymlog.dto.request.ExerciseRequest;
import com.gymlog.dto.response.ExerciseCategoryResponse;
import com.gymlog.dto.response.ExerciseResponse;
import com.gymlog.dto.response.PagedResponse;
import com.gymlog.entity.Exercise;
import com.gymlog.entity.ExerciseCategory;
import com.gymlog.exception.ResourceNotFoundException;
import com.gymlog.repository.ExerciseCategoryRepository;
import com.gymlog.repository.ExerciseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExerciseServiceImpl {

    private final ExerciseRepository exerciseRepository;
    private final ExerciseCategoryRepository categoryRepository;

    public PagedResponse<ExerciseResponse> getAllExercises(int page, int size) {
        Page<Exercise> exercisePage = exerciseRepository.findAll(
                PageRequest.of(page, size, Sort.by("name").ascending()));
        return mapToPagedResponse(exercisePage);
    }

    public ExerciseResponse getExerciseById(Long id) {
        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exercise", "id", id));
        return mapToResponse(exercise);
    }

    public PagedResponse<ExerciseResponse> getExercisesByCategory(Long categoryId, int page, int size) {
        Page<Exercise> exercisePage = exerciseRepository.findByCategoryId(categoryId,
                PageRequest.of(page, size, Sort.by("name").ascending()));
        return mapToPagedResponse(exercisePage);
    }

    public PagedResponse<ExerciseResponse> searchExercises(String query, int page, int size) {
        Page<Exercise> exercisePage = exerciseRepository.search(query,
                PageRequest.of(page, size, Sort.by("name").ascending()));
        return mapToPagedResponse(exercisePage);
    }

    @Transactional
    public ExerciseResponse createExercise(ExerciseRequest request) {
        Exercise exercise = new Exercise();
        mapRequestToEntity(request, exercise);
        exercise.setIsCustom(false);
        exercise = exerciseRepository.save(exercise);
        log.info("Exercise created: {}", exercise.getName());
        return mapToResponse(exercise);
    }

    @Transactional
    public ExerciseResponse updateExercise(Long id, ExerciseRequest request) {
        Exercise exercise = exerciseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exercise", "id", id));
        mapRequestToEntity(request, exercise);
        exercise = exerciseRepository.save(exercise);
        log.info("Exercise updated: {}", exercise.getName());
        return mapToResponse(exercise);
    }

    @Transactional
    public void deleteExercise(Long id) {
        if (!exerciseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Exercise", "id", id);
        }
        exerciseRepository.deleteById(id);
        log.info("Exercise deleted: {}", id);
    }

    public List<ExerciseCategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::mapCategoryToResponse)
                .toList();
    }

    @Transactional
    public ExerciseCategoryResponse createCategory(ExerciseCategory category) {
        category = categoryRepository.save(category);
        return mapCategoryToResponse(category);
    }

    private void mapRequestToEntity(ExerciseRequest request, Exercise exercise) {
        exercise.setName(request.getName());
        exercise.setDescription(request.getDescription());
        exercise.setInstructions(request.getInstructions());
        exercise.setDifficulty(request.getDifficulty());
        exercise.setMuscleGroup(request.getMuscleGroup());
        exercise.setEquipment(request.getEquipment());
        exercise.setImageUrl(request.getImageUrl());

        if (request.getCategoryId() != null) {
            ExerciseCategory category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", request.getCategoryId()));
            exercise.setCategory(category);
        }
    }

    private ExerciseResponse mapToResponse(Exercise exercise) {
        return ExerciseResponse.builder()
                .id(exercise.getId())
                .name(exercise.getName())
                .description(exercise.getDescription())
                .instructions(exercise.getInstructions())
                .difficulty(exercise.getDifficulty())
                .muscleGroup(exercise.getMuscleGroup())
                .equipment(exercise.getEquipment())
                .imageUrl(exercise.getImageUrl())
                .categoryId(exercise.getCategory() != null ? exercise.getCategory().getId() : null)
                .categoryName(exercise.getCategory() != null ? exercise.getCategory().getName() : null)
                .isCustom(exercise.getIsCustom())
                .build();
    }

    private ExerciseCategoryResponse mapCategoryToResponse(ExerciseCategory cat) {
        return ExerciseCategoryResponse.builder()
                .id(cat.getId())
                .name(cat.getName())
                .description(cat.getDescription())
                .icon(cat.getIcon())
                .build();
    }

    private PagedResponse<ExerciseResponse> mapToPagedResponse(Page<Exercise> page) {
        List<ExerciseResponse> exercises = page.getContent().stream()
                .map(this::mapToResponse)
                .toList();
        return PagedResponse.<ExerciseResponse>builder()
                .content(exercises)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
}
