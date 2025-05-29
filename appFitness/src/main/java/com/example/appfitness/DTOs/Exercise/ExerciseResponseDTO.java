package com.example.appfitness.DTOs.Exercise;

import com.example.appfitness.models.Exercise;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseResponseDTO {
    private int id;
    private String name;
    private Exercise.ExerciseType type;
    private String description;
    private String muscleGroup;
    //private String videoURL; // trabalho futuro
    //private String imageURL; // trabalho futuro


    public static ExerciseResponseDTO fromEntity(Exercise exercise) {
        ExerciseResponseDTO dto = new ExerciseResponseDTO();
        dto.setId(exercise.getId());
        dto.setName(exercise.getName());
        dto.setType(exercise.getType());
        dto.setDescription(exercise.getDescription());
        dto.setMuscleGroup(exercise.getMuscleGroup());
        //dto.setVideoURL(exercise.getVideoURL());
        //dto.setImageURL(exercise.getImageURL());
        return dto;
    }
}