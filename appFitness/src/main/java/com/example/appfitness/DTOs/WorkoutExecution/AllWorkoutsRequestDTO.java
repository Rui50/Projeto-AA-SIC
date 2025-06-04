package com.example.appfitness.DTOs.WorkoutExecution;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AllWorkoutsRequestDTO {
    private Integer userID; // prob not needed
    private Integer page;
    private Integer itemsPerPage;
    private String searchQuery;
    private LocalDate startDate;
    private LocalDate endDate;
    private String sortBy;
    private String sortOrder;
}
