package ru.itis.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestTravelDto {
    @NotNull(message = "Name cannot be null")
    private String name;
    @Min(value = 0, message = "Total budget must be positive")
    private Double totalBudget;
    @FutureOrPresent(message = "Date of begin must be in the present or future")
    private LocalDate dateOfBegin;
    @Future(message = "Date of end must be in the future")
    private LocalDate dateOfEnd;
    @Valid
    @Schema(description = "List of participant phone numbers. Each phone number must start with 8 and have 11 digits.",
            example = "[\"89876543210\", \"81234567890\"]")
    private List<@Pattern(regexp = "^8\\d{10}$", message = "The number must start with 8 and contain 11 digits.") String> participantPhones;
}
