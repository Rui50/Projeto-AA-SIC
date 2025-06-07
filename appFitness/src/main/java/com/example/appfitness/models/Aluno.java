package com.example.appfitness.models;

import jakarta.persistence.*;
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
@PrimaryKeyJoinColumn(name = "id")
@ToString
public class Aluno extends User  {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professor_id")
    @ToString.Exclude
    private Professor professor;

    // se decidirmos fazer os requests
}
