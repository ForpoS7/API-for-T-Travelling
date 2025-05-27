package ru.itis.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itis.api.dto.MessageDto;
import ru.itis.api.dto.RegistrationForm;
import ru.itis.api.service.RegistrationService;

@RestController
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user registration and authentication")
@RequestMapping("/api/v1/registration")
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping
    @Operation(
            summary = "Register a new user",
            description = "Creates a new user in the system",
            responses = {
                    @ApiResponse(responseCode = "201",
                            description = "User registered successfully",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = MessageDto.class))),
                    @ApiResponse(responseCode = "400",
                            description = "Invalid input data",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = MessageDto.class))),
            }
    )
    public ResponseEntity<RegistrationForm> registration(
            @Valid
            @RequestBody
            RegistrationForm dto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(registrationService.saveUser(dto));
    }
}
