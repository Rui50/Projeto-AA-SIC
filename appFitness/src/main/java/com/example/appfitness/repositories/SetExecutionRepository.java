package com.example.appfitness.repositories;

import com.example.appfitness.models.ExerciseExecution;
import com.example.appfitness.models.SetExecution;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SetExecutionRepository extends JpaRepository<SetExecution, Integer> {

    // obter os sets realizados de um exercicio realizado em ordem para nao ter de ordenar depois na frontend
    @Query("SELECT se FROM SetExecution se WHERE se.exerciseExecution.id = :exerciseExecutionId ORDER BY se.setNumber ASC")
    List<SetExecution> findByExerciseExecutionIdOrderBySetNumberAsc(Integer exerciseExecutionId);

    // obter um set específico, secalhar vai ser preciso para as dashboards se tivermos tempo de fazer uma coisa mais complicada
    @Query("SELECT se FROM SetExecution se WHERE se.exerciseExecution.id = :exerciseExecutionId AND se.setNumber = :setNumber")
    Optional<SetExecution> findByExerciseExecutionIdAndSetNumber(Integer exerciseExecutionId, Integer setNumber);
}