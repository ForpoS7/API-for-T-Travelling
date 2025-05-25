package ru.itis.api.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TravelDto {
    private Long id;
    @NotNull(message = "Name cannot be null")
    private String name;
    @Min(value = 0, message = "Total budget must be positive")
    private BigDecimal totalBudget;
    @FutureOrPresent(message = "Date of begin must be in the present or future")
    private LocalDate dateOfBegin;
    @Future(message = "Date of end must be in the future")
    private LocalDate dateOfEnd;
}
