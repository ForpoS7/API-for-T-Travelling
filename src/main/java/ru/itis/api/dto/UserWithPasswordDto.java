package ru.itis.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.experimental.Accessors;


@Data
@Accessors(chain = true)
@Schema(description = "User data including passwords used for update operations")
public class UserWithPasswordDto {

    @NotNull(message = "The last name must not be empty")
    @Schema(description = "First name of the user", example = "John")
    private String firstName;

    @NotNull(message = "The last name must not be empty")
    @Schema(description = "Last name of the user", example = "Doe")
    private String lastName;

    @Pattern(regexp = "^8\\d{10}$", message = "The number must start with 8 and contain 11 digits.")
    @NotBlank(message = "The number should not contain spaces")
    @NotNull(message = "The number must not be empty")
    @Schema(description = "Phone number of the user", example = "89876543210")
    private String phoneNumber;

    @Pattern(regexp = "^$|^(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d]{6,15}$", message = "The password must contain one uppercase letter and one number, and must be between 6 and 15 characters long.")
    @Schema(description = "Password for the user account", example = "Password123")
    private String password;

    @Schema(description = "Password confirmation field")
    private String confirmPassword;

}
