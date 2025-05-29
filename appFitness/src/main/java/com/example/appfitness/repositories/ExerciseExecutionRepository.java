package com.example.appfitness.repositories;

import com.example.appfitness.models.ExerciseData;
import com.example.appfitness.models.ExerciseExecution;
import com.example.appfitness.models.WorkoutExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExerciseExecutionRepository extends JpaRepository<ExerciseExecution, Integer> {

    // pegar nos exercise execution de um workout feito
    @Query("SELECT ee FROM ExerciseExecution ee WHERE ee.workoutExecution.id = :workoutExecutionId")
    List<ExerciseExecution> findByWorkoutExecutionId(Integer workoutExecutionId);

    // nao deve ser preciso -> pegar num exercise execution de um exercicio específico
    @Query("SELECT ee FROM ExerciseExecution ee WHERE ee.workoutExecution.id = :workoutExecutionId AND ee.exerciseData.id = :exerciseDataId")
    Optional<ExerciseExecution> findByWorkoutExecutionIdAndExerciseDataId(Integer workoutExecutionId, Integer exerciseDataId);
}
