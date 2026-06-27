package com.gymlog.config;

import com.gymlog.entity.*;
import com.gymlog.enums.*;
import com.gymlog.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ExerciseCategoryRepository categoryRepository;
    private final ExerciseRepository exerciseRepository;
    private final WorkoutRepository workoutRepository;
    private final NutritionLogRepository nutritionLogRepository;
    private final GoalRepository goalRepository;
    private final PersonalRecordRepository personalRecordRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already initialized, skipping seed data");
            return;
        }

        log.info("Initializing seed data...");
        User demo = seedUsers();
        seedExercises();
        seedDemoWorkouts(demo);
        seedDemoNutrition(demo);
        seedDemoGoals(demo);
        seedDemoPersonalRecords(demo);
        log.info("Seed data initialized successfully!");
    }

    private User seedUsers() {
        User admin = User.builder()
                .email("admin@gymlog.com")
                .password(passwordEncoder.encode("Admin@123"))
                .firstName("Admin")
                .lastName("User")
                .role(Role.ADMIN)
                .dailyCalorieTarget(2500)
                .build();
        userRepository.save(admin);

        User demo = User.builder()
                .email("demo@gymlog.com")
                .password(passwordEncoder.encode("Demo@123"))
                .firstName("John")
                .lastName("Doe")
                .role(Role.USER)
                .height(175.0)
                .currentWeight(78.0)
                .dateOfBirth(LocalDate.of(2000, 5, 15))
                .gender("Male")
                .dailyCalorieTarget(2200)
                .build();
        userRepository.save(demo);
        log.info("Seeded 2 users (admin + demo)");
        return demo;
    }

    private void seedExercises() {
        // Categories
        ExerciseCategory chest = saveCategory("Chest", "Chest exercises", "💪");
        ExerciseCategory back = saveCategory("Back", "Back exercises", "🔙");
        ExerciseCategory shoulders = saveCategory("Shoulders", "Shoulder exercises", "🤸");
        ExerciseCategory arms = saveCategory("Arms", "Bicep and tricep exercises", "💪");
        ExerciseCategory legs = saveCategory("Legs", "Leg exercises", "🦵");
        ExerciseCategory core = saveCategory("Core", "Abdominal and core exercises", "🏋️");
        ExerciseCategory cardio = saveCategory("Cardio", "Cardiovascular exercises", "🏃");
        ExerciseCategory fullBody = saveCategory("Full Body", "Full body compound exercises", "🏋️‍♂️");

        // Chest exercises
        saveExercise("Barbell Bench Press", "Classic chest compound exercise", "Lie on a flat bench, grip the barbell slightly wider than shoulder width, lower to chest, press up.", Difficulty.INTERMEDIATE, "Chest", "Barbell, Bench", chest);
        saveExercise("Incline Dumbbell Press", "Upper chest focused press", "Set bench to 30-45 degrees, press dumbbells from chest to lockout.", Difficulty.INTERMEDIATE, "Chest", "Dumbbells, Incline Bench", chest);
        saveExercise("Dumbbell Flyes", "Chest isolation exercise", "Lie on flat bench, extend arms with slight bend, lower dumbbells in arc motion.", Difficulty.BEGINNER, "Chest", "Dumbbells, Bench", chest);
        saveExercise("Cable Crossover", "Cable chest isolation", "Stand between cable stations, bring handles together in front of chest.", Difficulty.INTERMEDIATE, "Chest", "Cable Machine", chest);
        saveExercise("Push-Ups", "Bodyweight chest exercise", "Start in plank position, lower chest to floor, push back up.", Difficulty.BEGINNER, "Chest", "Bodyweight", chest);
        saveExercise("Decline Bench Press", "Lower chest focused press", "Lie on decline bench, press barbell from lower chest.", Difficulty.INTERMEDIATE, "Chest", "Barbell, Decline Bench", chest);

        // Back exercises
        saveExercise("Deadlift", "King of all exercises", "Stand with feet hip-width, grip barbell, drive through heels, stand tall.", Difficulty.ADVANCED, "Back", "Barbell", back);
        saveExercise("Pull-Ups", "Classic back exercise", "Hang from bar with overhand grip, pull chin above bar.", Difficulty.INTERMEDIATE, "Back", "Pull-Up Bar", back);
        saveExercise("Barbell Rows", "Compound back exercise", "Hinge at hips, pull barbell to lower chest.", Difficulty.INTERMEDIATE, "Back", "Barbell", back);
        saveExercise("Lat Pulldown", "Cable back exercise", "Sit at pulldown machine, pull bar to upper chest.", Difficulty.BEGINNER, "Back", "Cable Machine", back);
        saveExercise("Seated Cable Row", "Mid-back exercise", "Sit at cable row station, pull handle to abdomen.", Difficulty.BEGINNER, "Back", "Cable Machine", back);
        saveExercise("T-Bar Row", "Thick back builder", "Straddle T-bar, row weight to chest.", Difficulty.INTERMEDIATE, "Back", "T-Bar", back);

        // Shoulder exercises
        saveExercise("Overhead Press", "Compound shoulder exercise", "Press barbell from shoulders to overhead lockout.", Difficulty.INTERMEDIATE, "Shoulders", "Barbell", shoulders);
        saveExercise("Lateral Raises", "Side delt isolation", "Raise dumbbells to side until arms are parallel to floor.", Difficulty.BEGINNER, "Shoulders", "Dumbbells", shoulders);
        saveExercise("Front Raises", "Front delt isolation", "Raise dumbbells in front to shoulder height.", Difficulty.BEGINNER, "Shoulders", "Dumbbells", shoulders);
        saveExercise("Face Pulls", "Rear delt and rotator cuff", "Pull rope attachment to face level with external rotation.", Difficulty.BEGINNER, "Shoulders", "Cable Machine", shoulders);
        saveExercise("Arnold Press", "Rotational shoulder press", "Start with palms facing you, rotate and press overhead.", Difficulty.INTERMEDIATE, "Shoulders", "Dumbbells", shoulders);

        // Arm exercises
        saveExercise("Barbell Curl", "Bicep mass builder", "Curl barbell from thighs to shoulders with control.", Difficulty.BEGINNER, "Biceps", "Barbell", arms);
        saveExercise("Hammer Curl", "Brachialis and forearm builder", "Curl dumbbells with neutral grip.", Difficulty.BEGINNER, "Biceps", "Dumbbells", arms);
        saveExercise("Tricep Pushdown", "Tricep isolation", "Push cable bar down until arms are fully extended.", Difficulty.BEGINNER, "Triceps", "Cable Machine", arms);
        saveExercise("Skull Crushers", "Tricep mass builder", "Lie on bench, lower EZ bar to forehead, extend arms.", Difficulty.INTERMEDIATE, "Triceps", "EZ Bar, Bench", arms);
        saveExercise("Concentration Curl", "Bicep peak builder", "Sit on bench, curl dumbbell with elbow braced on inner thigh.", Difficulty.BEGINNER, "Biceps", "Dumbbell", arms);
        saveExercise("Overhead Tricep Extension", "Long head tricep focus", "Hold dumbbell overhead, lower behind head, extend.", Difficulty.BEGINNER, "Triceps", "Dumbbell", arms);

        // Leg exercises
        saveExercise("Barbell Squat", "King of leg exercises", "Bar on upper back, squat to parallel or below, stand up.", Difficulty.INTERMEDIATE, "Quadriceps", "Barbell, Squat Rack", legs);
        saveExercise("Romanian Deadlift", "Hamstring focused", "Hold barbell, hinge at hips keeping legs slightly bent.", Difficulty.INTERMEDIATE, "Hamstrings", "Barbell", legs);
        saveExercise("Leg Press", "Machine quad exercise", "Push platform away with feet shoulder-width.", Difficulty.BEGINNER, "Quadriceps", "Leg Press Machine", legs);
        saveExercise("Leg Curl", "Hamstring isolation", "Lie face down, curl weight toward glutes.", Difficulty.BEGINNER, "Hamstrings", "Leg Curl Machine", legs);
        saveExercise("Leg Extension", "Quad isolation", "Sit at machine, extend legs to lockout.", Difficulty.BEGINNER, "Quadriceps", "Leg Extension Machine", legs);
        saveExercise("Calf Raises", "Calf builder", "Stand on edge of platform, raise heels.", Difficulty.BEGINNER, "Calves", "Bodyweight", legs);
        saveExercise("Bulgarian Split Squat", "Unilateral leg exercise", "Rear foot on bench, squat down on front leg.", Difficulty.INTERMEDIATE, "Quadriceps", "Dumbbells, Bench", legs);
        saveExercise("Hip Thrust", "Glute builder", "Back against bench, drive hips up with barbell on lap.", Difficulty.INTERMEDIATE, "Glutes", "Barbell, Bench", legs);

        // Core exercises
        saveExercise("Plank", "Core stabilization", "Hold push-up position on forearms, keep body straight.", Difficulty.BEGINNER, "Core", "Bodyweight", core);
        saveExercise("Crunches", "Upper abs", "Lie on back, curl shoulders off floor.", Difficulty.BEGINNER, "Abs", "Bodyweight", core);
        saveExercise("Hanging Leg Raises", "Lower abs", "Hang from bar, raise legs to parallel.", Difficulty.INTERMEDIATE, "Abs", "Pull-Up Bar", core);
        saveExercise("Russian Twists", "Obliques", "Sit with feet off ground, rotate torso side to side.", Difficulty.BEGINNER, "Obliques", "Bodyweight", core);
        saveExercise("Ab Wheel Rollout", "Advanced core", "Kneel, roll ab wheel forward, return to start.", Difficulty.ADVANCED, "Core", "Ab Wheel", core);
        saveExercise("Cable Woodchop", "Rotational core", "Pull cable diagonally across body.", Difficulty.INTERMEDIATE, "Obliques", "Cable Machine", core);

        // Cardio exercises
        saveExercise("Running", "Cardiovascular endurance", "Run at steady pace or intervals.", Difficulty.BEGINNER, "Cardio", "Treadmill", cardio);
        saveExercise("Cycling", "Low impact cardio", "Cycle at moderate to high intensity.", Difficulty.BEGINNER, "Cardio", "Stationary Bike", cardio);
        saveExercise("Jump Rope", "High intensity cardio", "Skip rope at steady rhythm.", Difficulty.BEGINNER, "Cardio", "Jump Rope", cardio);
        saveExercise("Rowing", "Full body cardio", "Row at steady pace on rowing machine.", Difficulty.INTERMEDIATE, "Cardio", "Rowing Machine", cardio);
        saveExercise("Stair Climber", "Lower body cardio", "Climb stairs at steady pace.", Difficulty.BEGINNER, "Cardio", "Stair Machine", cardio);

        // Full Body
        saveExercise("Clean and Press", "Olympic lift variation", "Clean barbell to shoulders, press overhead.", Difficulty.ADVANCED, "Full Body", "Barbell", fullBody);
        saveExercise("Burpees", "Full body conditioning", "Squat, jump back to plank, push-up, jump up.", Difficulty.INTERMEDIATE, "Full Body", "Bodyweight", fullBody);
        saveExercise("Kettlebell Swing", "Hip hinge power", "Hinge at hips, swing kettlebell to eye level.", Difficulty.INTERMEDIATE, "Full Body", "Kettlebell", fullBody);
        saveExercise("Thrusters", "Squat to press combo", "Front squat into overhead press.", Difficulty.INTERMEDIATE, "Full Body", "Barbell", fullBody);

        log.info("Seeded {} exercises across {} categories", exerciseRepository.count(), categoryRepository.count());
    }

    private void seedDemoWorkouts(User demo) {
        Exercise benchPress = exerciseRepository.findByName("Barbell Bench Press").orElseThrow();
        Exercise inclinePress = exerciseRepository.findByName("Incline Dumbbell Press").orElseThrow();
        Exercise skullCrushers = exerciseRepository.findByName("Skull Crushers").orElseThrow();
        Exercise barbellCurl = exerciseRepository.findByName("Barbell Curl").orElseThrow();
        Exercise deadlift = exerciseRepository.findByName("Deadlift").orElseThrow();
        Exercise pullups = exerciseRepository.findByName("Pull-Ups").orElseThrow();

        LocalDate today = LocalDate.now();
        LocalDate monday = today.minusDays(today.getDayOfWeek().getValue() - 1);

        Workout chestTriceps = Workout.builder()
                .user(demo)
                .name("Chest + Triceps")
                .notes("Heavy compound chest and triceps day")
                .workoutDate(monday)
                .durationMinutes(90)
                .status(WorkoutStatus.COMPLETED)
                .exercises(List.of(
                        createWorkoutExercise(null, benchPress, List.of(
                                createWorkoutSet(null, 1, 8, 90.0, false, true),
                                createWorkoutSet(null, 2, 8, 90.0, false, true),
                                createWorkoutSet(null, 3, 6, 95.0, false, true)
                        )),
                        createWorkoutExercise(null, inclinePress, List.of(
                                createWorkoutSet(null, 1, 8, 60.0, false, true),
                                createWorkoutSet(null, 2, 8, 62.5, false, true)
                        )),
                        createWorkoutExercise(null, skullCrushers, List.of(
                                createWorkoutSet(null, 1, 10, 40.0, false, true),
                                createWorkoutSet(null, 2, 10, 42.5, false, true)
                        ))
                ))
                .build();
        chestTriceps.getExercises().forEach(ex -> ex.setWorkout(chestTriceps));
        chestTriceps.getExercises().forEach(ex -> ex.getSets().forEach(set -> set.setWorkoutExercise(ex)));
        workoutRepository.save(chestTriceps);

        Workout backBiceps = Workout.builder()
                .user(demo)
                .name("Back + Biceps")
                .notes("Pull day with focus on width and arm strength")
                .workoutDate(monday.plusDays(1))
                .durationMinutes(80)
                .status(WorkoutStatus.COMPLETED)
                .exercises(List.of(
                        createWorkoutExercise(null, deadlift, List.of(
                                createWorkoutSet(null, 1, 5, 140.0, false, true),
                                createWorkoutSet(null, 2, 5, 145.0, false, true)
                        )),
                        createWorkoutExercise(null, pullups, List.of(
                                createWorkoutSet(null, 1, 8, null, false, true),
                                createWorkoutSet(null, 2, 7, null, false, true)
                        )),
                        createWorkoutExercise(null, barbellCurl, List.of(
                                createWorkoutSet(null, 1, 12, 25.0, false, true),
                                createWorkoutSet(null, 2, 12, 27.5, false, true)
                        ))
                ))
                .build();
        backBiceps.getExercises().forEach(ex -> ex.setWorkout(backBiceps));
        backBiceps.getExercises().forEach(ex -> ex.getSets().forEach(set -> set.setWorkoutExercise(ex)));
        workoutRepository.save(backBiceps);
    }

    private void seedDemoNutrition(User demo) {
        LocalDate today = LocalDate.now();
        nutritionLogRepository.save(NutritionLog.builder()
                .user(demo)
                .foodName("Oatmeal with berries")
                .calories(420.0)
                .protein(18.0)
                .carbs(58.0)
                .fat(10.0)
                .mealType(MealType.BREAKFAST)
                .logDate(today)
                .build());
        nutritionLogRepository.save(NutritionLog.builder()
                .user(demo)
                .foodName("Chicken breast and rice")
                .calories(610.0)
                .protein(55.0)
                .carbs(65.0)
                .fat(15.0)
                .mealType(MealType.LUNCH)
                .logDate(today)
                .build());
        nutritionLogRepository.save(NutritionLog.builder()
                .user(demo)
                .foodName("Greek yogurt and almonds")
                .calories(320.0)
                .protein(22.0)
                .carbs(22.0)
                .fat(15.0)
                .mealType(MealType.SNACK)
                .logDate(today)
                .build());
    }

    private void seedDemoGoals(User demo) {
        goalRepository.save(Goal.builder()
                .user(demo)
                .goalType(GoalType.STRENGTH)
                .title("Increase bench press")
                .description("Hit a 100kg bench press for 5 reps.")
                .targetValue(100.0)
                .currentValue(85.0)
                .unit("kg")
                .startDate(LocalDate.now().minusWeeks(2))
                .targetDate(LocalDate.now().plusWeeks(6))
                .status(GoalStatus.ACTIVE)
                .build());
    }

    private void seedDemoPersonalRecords(User demo) {
        Exercise bench = exerciseRepository.findByName("Barbell Bench Press").orElseThrow();
        personalRecordRepository.save(PersonalRecord.builder()
                .user(demo)
                .exercise(bench)
                .maxWeight(95.0)
                .maxReps(5)
                .maxVolume(475.0)
                .achievedDate(LocalDate.now().minusDays(5))
                .build());
    }

    private WorkoutExercise createWorkoutExercise(Workout workout, Exercise exercise, List<WorkoutSet> sets) {
        WorkoutExercise workoutExercise = WorkoutExercise.builder()
                .workout(workout)
                .exercise(exercise)
                .orderIndex(null)
                .notes(null)
                .sets(sets)
                .build();
        return workoutExercise;
    }

    private WorkoutSet createWorkoutSet(WorkoutExercise workoutExercise, int setNumber, Integer reps, Double weight, Boolean isWarmup, Boolean completed) {
        return WorkoutSet.builder()
                .workoutExercise(workoutExercise)
                .setNumber(setNumber)
                .reps(reps)
                .weight(weight)
                .durationSeconds(null)
                .isWarmup(isWarmup)
                .completed(completed)
                .build();
    }

    private ExerciseCategory saveCategory(String name, String description, String icon) {
        return categoryRepository.save(ExerciseCategory.builder()
                .name(name).description(description).icon(icon).build());
    }

    private Exercise saveExercise(String name, String description, String instructions,
                               Difficulty difficulty, String muscleGroup, String equipment,
                               ExerciseCategory category) {
        return exerciseRepository.save(Exercise.builder()
                .name(name).description(description).instructions(instructions)
                .difficulty(difficulty).muscleGroup(muscleGroup).equipment(equipment)
                .category(category).isCustom(false).build());
    }
}
