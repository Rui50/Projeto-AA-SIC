package com.example.appfitness.DTOs.Progress;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ChartDataDTO {
    private LocalDate date;
    private Double value;
}
