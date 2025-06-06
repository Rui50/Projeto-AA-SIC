package com.example.appfitness.repositories;

import com.example.appfitness.models.Exercise;
import com.example.appfitness.models.ExerciseData;
import com.example.appfitness.models.WorkoutPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExerciseDataRepository extends JpaRepository<ExerciseData, Integer> {
    // devolve os exercise data de um workoutPlan
    @Query("SELECT ed FROM ExerciseData ed WHERE ed.workoutPlan.id = :workoutPlanId")
    List<ExerciseData> findByWorkoutPlanId(Integer workoutPlanId);

    @Query("SELECT ed FROM ExerciseData ed WHERE ed.workoutPlan.id = :workoutPlanId AND ed.exercise.id = :exerciseId")
    List<ExerciseData> findByWorkoutPlanIdAndExerciseId(Integer workoutPlanId, Integer exerciseId);

    @Query("SELECT ed FROM ExerciseData ed WHERE ed.id = :id AND ed.isDeleted = false")
    Optional<ExerciseData> findByIdAndNotDeleted(Integer id);

    // Find all ExerciseData for a workout plan that are not deleted
    @Query("SELECT ed FROM ExerciseData ed WHERE ed.workoutPlan.id = :workoutPlanId AND ed.isDeleted = false ORDER BY ed.id")
    List<ExerciseData> findByWorkoutPlanIdAndNotDeleted(Integer workoutPlanId);
}
