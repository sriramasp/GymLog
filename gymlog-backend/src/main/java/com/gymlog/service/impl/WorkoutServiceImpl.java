package com.gymlog.service.impl;

import com.gymlog.dto.request.WorkoutExerciseRequest;
import com.gymlog.dto.request.WorkoutRequest;
import com.gymlog.dto.request.WorkoutSetRequest;
import com.gymlog.dto.response.*;
import com.gymlog.entity.*;
import com.gymlog.enums.WorkoutStatus;
import com.gymlog.exception.ResourceNotFoundException;
import com.gymlog.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkoutServiceImpl {

    private final WorkoutRepository workoutRepository;
    private final ExerciseRepository exerciseRepository;
    private final UserRepository userRepository;

    @Transactional
    public WorkoutResponse createWorkout(String email, WorkoutRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        Workout workout = Workout.builder()
                .user(user)
                .name(request.getName())
                .notes(request.getNotes())
                .workoutDate(request.getWorkoutDate())
                .durationMinutes(request.getDurationMinutes())
                .status(request.getStatus() != null ? request.getStatus() : WorkoutStatus.PLANNED)
                .exercises(new ArrayList<>())
                .build();

        buildWorkoutExercises(workout, request.getExercises());

        workout = workoutRepository.save(workout);
        log.info("Workout created: {} for user: {}", workout.getName(), email);
        return mapToResponse(workout);
    }

    public WorkoutResponse getWorkoutById(Long id, String email) {
        Workout workout = workoutRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Workout", "id", id));
        if (!workout.getUser().getEmail().equals(email)) {
            throw new org.springframework.security.access.AccessDeniedException("You do not have permission to access this workout");
        }
        return mapToResponse(workout);
    }

    public PagedResponse<WorkoutResponse> getWorkoutHistory(String email, int page, int size) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        Page<Workout> workoutPage = workoutRepository.findByUserIdOrderByWorkoutDateDesc(
                user.getId(), PageRequest.of(page, size));

        List<WorkoutResponse> workouts = workoutPage.getContent().stream()
                .map(this::mapToResponse)
                .toList();

        return PagedResponse.<WorkoutResponse>builder()
                .content(workouts)
                .page(workoutPage.getNumber())
                .size(workoutPage.getSize())
                .totalElements(workoutPage.getTotalElements())
                .totalPages(workoutPage.getTotalPages())
                .last(workoutPage.isLast())
                .build();
    }

    public List<WorkoutResponse> getWorkoutsByDate(String email, LocalDate date) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return workoutRepository.findByUserIdAndWorkoutDate(user.getId(), date).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public WorkoutResponse updateWorkout(Long id, String email, WorkoutRequest request) {
        Workout workout = workoutRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Workout", "id", id));

        workout.setName(request.getName());
        workout.setNotes(request.getNotes());
        workout.setWorkoutDate(request.getWorkoutDate());
        workout.setDurationMinutes(request.getDurationMinutes());
        if (request.getStatus() != null) workout.setStatus(request.getStatus());

        // Clear and rebuild exercises if provided
        if (request.getExercises() != null) {
            workout.getExercises().clear();
            buildWorkoutExercises(workout, request.getExercises());
        }

        workout = workoutRepository.save(workout);
        log.info("Workout updated: {}", id);
        return mapToResponse(workout);
    }

    @Transactional
    public void deleteWorkout(Long id, String email) {
        Workout workout = workoutRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Workout", "id", id));
        if (!workout.getUser().getEmail().equals(email)) {
            throw new org.springframework.security.access.AccessDeniedException("You do not have permission to delete this workout");
        }
        workoutRepository.delete(workout);
        log.info("Workout deleted: {}", id);
    }

    private void buildWorkoutExercises(Workout workout, List<WorkoutExerciseRequest> exerciseRequests) {
        if (exerciseRequests != null) {
            for (int i = 0; i < exerciseRequests.size(); i++) {
                WorkoutExerciseRequest exReq = exerciseRequests.get(i);
                Exercise exercise = exerciseRepository.findById(exReq.getExerciseId())
                        .orElseThrow(() -> new ResourceNotFoundException("Exercise", "id", exReq.getExerciseId()));

                WorkoutExercise we = WorkoutExercise.builder()
                        .workout(workout)
                        .exercise(exercise)
                        .orderIndex(exReq.getOrderIndex() != null ? exReq.getOrderIndex() : i)
                        .notes(exReq.getNotes())
                        .sets(new ArrayList<>())
                        .build();

                if (exReq.getSets() != null) {
                    for (int j = 0; j < exReq.getSets().size(); j++) {
                        WorkoutSetRequest setReq = exReq.getSets().get(j);
                        WorkoutSet ws = WorkoutSet.builder()
                                .workoutExercise(we)
                                .setNumber(setReq.getSetNumber() != null ? setReq.getSetNumber() : j + 1)
                                .reps(setReq.getReps())
                                .weight(setReq.getWeight())
                                .durationSeconds(setReq.getDurationSeconds())
                                .isWarmup(setReq.getIsWarmup() != null ? setReq.getIsWarmup() : false)
                                .completed(setReq.getCompleted() != null ? setReq.getCompleted() : true)
                                .build();
                        we.getSets().add(ws);
                    }
                }
                workout.getExercises().add(we);
            }
        }
    }

    private WorkoutResponse mapToResponse(Workout workout) {
        List<WorkoutExerciseResponse> exerciseResponses = workout.getExercises().stream()
                .map(this::mapExerciseToResponse)
                .toList();

        int totalSets = exerciseResponses.stream()
                .mapToInt(e -> e.getSets() != null ? e.getSets().size() : 0)
                .sum();

        double totalVolume = workout.getExercises().stream()
                .flatMap(we -> we.getSets().stream())
                .filter(s -> s.getWeight() != null && s.getReps() != null)
                .mapToDouble(s -> s.getWeight() * s.getReps())
                .sum();

        return WorkoutResponse.builder()
                .id(workout.getId())
                .name(workout.getName())
                .notes(workout.getNotes())
                .workoutDate(workout.getWorkoutDate())
                .durationMinutes(workout.getDurationMinutes())
                .status(workout.getStatus())
                .exercises(exerciseResponses)
                .createdAt(workout.getCreatedAt())
                .totalExercises(exerciseResponses.size())
                .totalSets(totalSets)
                .totalVolume(totalVolume)
                .build();
    }

    private WorkoutExerciseResponse mapExerciseToResponse(WorkoutExercise we) {
        List<WorkoutSetResponse> setResponses = we.getSets().stream()
                .map(s -> WorkoutSetResponse.builder()
                        .id(s.getId())
                        .setNumber(s.getSetNumber())
                        .reps(s.getReps())
                        .weight(s.getWeight())
                        .durationSeconds(s.getDurationSeconds())
                        .isWarmup(s.getIsWarmup())
                        .completed(s.getCompleted())
                        .build())
                .toList();

        return WorkoutExerciseResponse.builder()
                .id(we.getId())
                .exerciseId(we.getExercise().getId())
                .exerciseName(we.getExercise().getName())
                .muscleGroup(we.getExercise().getMuscleGroup())
                .orderIndex(we.getOrderIndex())
                .notes(we.getNotes())
                .sets(setResponses)
                .build();
    }
}
