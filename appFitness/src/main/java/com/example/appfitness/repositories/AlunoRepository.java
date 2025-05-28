package com.example.appfitness.repositories;

import com.example.appfitness.models.Aluno;
import com.example.appfitness.models.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Integer> {
    // encontrar todos os alunos de um professor

    @Query("SELECT a FROM Aluno a WHERE a.professor.id = :professor_id")
    Optional<List<Aluno>> findByProfessor(Integer professor_id);


    @Query("SELECT a FROM Aluno a WHERE a.email = :email")
    Optional<Aluno> findByEmail(String email);

    @Query("SELECT a FROM Aluno a WHERE a.professor IS NULL")
    List<Aluno> findAlunosWithoutProfessor();
}
