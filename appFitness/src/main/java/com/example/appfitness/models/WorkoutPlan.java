package com.example.appfitness.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "WorkoutPlan")
public class WorkoutPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String description;

    private Integer createdBy;
    private LocalDate updatedAt;

    @Column(nullable = false)
    private boolean active;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkoutScheduleType scheduleType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    @ToString.Exclude
    private User owner;

    @OneToMany(mappedBy = "workoutPlan", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @ToString.Exclude
    private List<ExerciseData> exercises = new ArrayList<>();

    @ElementCollection(targetClass = DayOfWeek.class)
    @CollectionTable(name = "workout_plan_scheduled_days", joinColumns = @JoinColumn(name = "workout_plan_id"))
    @Enumerated(EnumType.STRING)
    private Set<DayOfWeek> scheduledDays;

    @Column(name = "is_deleted")
    private boolean isDeleted = false;

    public void addExercise(ExerciseData exercise) {
        if (this.exercises == null) {
            this.exercises = new ArrayList<>();
        }
        this.exercises.add(exercise);
        exercise.setWorkoutPlan(this);
    }

    public void removeExercise(ExerciseData exercise) {
        if (this.exercises != null) {
            this.exercises.remove(exercise);
            exercise.setWorkoutPlan(null);
        }
    }

    public enum WorkoutScheduleType {
        Fixed,
        Free
    }
}
