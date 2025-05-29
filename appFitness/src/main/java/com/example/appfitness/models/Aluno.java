package com.example.appfitness.models;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;
//https://stackoverflow.com/questions/57455379/implementing-model-inheritance-on-springboot-have-strange-data-repository-behavi

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper=true)
@SuperBuilder
@Table(name="Aluno")
public class Aluno extends User  {

    @ManyToOne
    @JoinColumn(name = "professor_id")
    private Professor professor;


    // se decidirmos fazer os requests
}
