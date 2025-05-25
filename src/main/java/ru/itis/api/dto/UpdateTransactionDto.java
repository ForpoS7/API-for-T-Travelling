package ru.itis.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import ru.itis.api.dictionary.Category;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Schema(description = "Data Transfer Object for updating an existing transaction")
public class UpdateTransactionDto {
    @Schema(description = "Updated total cost of the transaction", example = "5000.00")
    @DecimalMin(value = "0.00", inclusive = false, message = "Total cost must be greater than zero")
    @Digits(integer = 10, fraction = 2, message = "Total cost must have up to 10 digits before the decimal and 2 after")
    private BigDecimal totalCost;

    @Schema(description = "Optional updated description of the transaction", example = "Hotel payment in Sochi")
    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;

    @Schema(description = "Updated category of the transaction", example = "HOTELS")
    @NotNull(message = "Category is required")
    private Category category;

    @Schema(description = "Updated list of users involved in this transaction with their shares")
    @Valid
    @NotEmpty(message = "At least one participant is required")
    private List<@Valid RequestUserTransactionDto> participant;
}
