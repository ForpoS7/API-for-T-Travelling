package ru.itis.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itis.api.dictionary.Category;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Data Transfer Object for creating a new transaction")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestTransactionDto {
    @Schema(description = "Category of the transaction", example = "HOTELS")
    @NotNull(message = "Category cannot be null")
    private Category category;

    @Schema(description = "Total cost of the transaction", example = "10000.00")
    @Digits(integer = 10, fraction = 2, message = "Total cost must have up to 10 digits before the decimal and 2 after")
    @DecimalMin(value = "0.00", inclusive = false, message = "Total cost must be greater than zero")
    private BigDecimal totalCost;

    @Schema(description = "Optional description of the transaction", example = "Tickets to Sochi")
    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;

    @Schema(description = "Date and time when the transaction was created", example = "2025-04-05T10:00:00")
    @PastOrPresent(message = "Date of begin must be in the present or past")
    private LocalDateTime createdAt;

    @Schema(description = "List of users involved in this transaction with their shares")
    @Valid
    @NotEmpty(message = "At least one participant is required")
    private List<@Valid RequestUserTransactionDto> participant;
}
