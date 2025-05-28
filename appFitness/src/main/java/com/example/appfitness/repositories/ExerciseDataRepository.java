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

@Repository
public interface ExerciseDataRepository extends JpaRepository<ExerciseData, Integer> {
    // devolve os exercise data de um workoutPlan
    @Query("SELECT ed FROM ExerciseData ed WHERE ed.workoutPlan = :workoutPlan")
    List<ExerciseData> findByWorkoutPlan(WorkoutPlan workoutPlan);

    @Query("SELECT ed FROM ExerciseData ed WHERE ed.workoutPlan = :workoutPlan AND ed.exercise = :exercise")
    List<ExerciseData> findByWorkoutPlanAndExerciseSpecification(WorkoutPlan workoutPlan, Exercise exercise);

}
