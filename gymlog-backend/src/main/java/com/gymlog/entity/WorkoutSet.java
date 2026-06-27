package com.gymlog.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "workout_sets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkoutSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_exercise_id", nullable = false)
    private WorkoutExercise workoutExercise;

    @Column(name = "set_number", nullable = false)
    private Integer setNumber;

    private Integer reps;

    private Double weight;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    @Column(name = "is_warmup")
    @Builder.Default
    private Boolean isWarmup = false;

    @Builder.Default
    private Boolean completed = true;
}
