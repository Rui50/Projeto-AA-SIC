package com.example.appfitness.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
@Table(name = "workout_plans")
public class WorkoutPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id;

    private String name;
    private String description;

    // nao deve ser preciso dados de quem criou mas apenas apresentar quem criou
    // fazer isso fica para trabalho futuro
    private Integer createdBy;
    private LocalTime updatedAt;

    @Column(nullable = false)
    private boolean active;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkoutScheduleType scheduleType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @OneToMany(mappedBy = "workoutPlan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ExerciseData> exercises = new ArrayList<>();

    @ElementCollection(targetClass = DayOfWeek.class)
    @CollectionTable(name = "workout_plan_scheduled_days", joinColumns = @JoinColumn(name = "workout_plan_id"))
    @Enumerated(EnumType.STRING)
    private Set<DayOfWeek> scheduledDays;

    public enum WorkoutScheduleType {
        FIXED,
        FREE
    }
}
