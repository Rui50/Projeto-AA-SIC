package com.example.appfitness.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Table(name = "Professor")
@PrimaryKeyJoinColumn(name = "id")
public class Professor extends User{
    @OneToMany(mappedBy = "professor", fetch = FetchType.LAZY)
    private List<Aluno> alunos;

    // planos de treino que ele criou
    @OneToMany(mappedBy = "createdBy", fetch = FetchType.LAZY)
    private List<WorkoutPlan> createdWorkoutPlans = new ArrayList<>();

    // trabalho futuro

    // pedidos de professor recebidos por user

    // Templates de treino


}

