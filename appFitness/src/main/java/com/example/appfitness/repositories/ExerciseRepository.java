package com.example.appfitness.repositories;

import com.example.appfitness.models.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Integer> {

    /*

    // isto sao tudo filtros que provalmente serão feitos no frontend?
    @Query("SELECT e FROM Exercise e WHERE e.name = :name")
    Optional<Exercise> findByName(String name);

    @Query("SELECT e FROM Exercise e WHERE e.type = :type")
    List<Exercise> findByExerciseType(Exercise.ExerciseType type);

    @Query("SELECT e FROM Exercise e WHERE e.muscleGroup = :muscleGroup")
    List<Exercise> findByMuscleGroup(Exercise.MuscleGroup muscleGroup);

     */
}
