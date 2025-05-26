package ru.itis.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Schema(description = "User share data in a transaction")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class RequestUserTransactionDto {
    @Schema(description = "Phone number of the user", example = "89876543210")
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^8\\d{10}$", message = "The number must start with 8 and contain 11 digits.")
    private String phoneNumber;

    @Schema(description = "Amount the user owes", example = "1000.00")
    @DecimalMin(value = "0.00", message = "Share amount must be at least 0")
    private BigDecimal shareAmount;
}
