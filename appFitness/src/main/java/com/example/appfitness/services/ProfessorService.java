package com.example.appfitness.services;

import com.example.appfitness.models.Professor;
import com.example.appfitness.repositories.ProfessorRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProfessorService {

    // secalhar juntamos aos Aluno ou user em si

    private ProfessorRepository professorRepository;

    public ProfessorService(ProfessorRepository professorRepository) {
        this.professorRepository = professorRepository;
    }

    public Optional<Professor> getProfessorById(int id) {
        return professorRepository.findById(id);
    }

    // se fizermos com que um user possa requisitar um professor
    public List<Professor> getAllProfessors() {
        return professorRepository.findAll();
    }


}
