package ru.itis.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import ru.itis.api.dto.DeviceTokenDto;
import ru.itis.api.dto.MessageDto;
import ru.itis.api.dto.UpdateUserDto;
import ru.itis.api.dto.UserDto;
import ru.itis.api.security.details.UserDetailsImpl;
import ru.itis.api.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/profile")
@Tag(name = "User Profile",
        description = "Operations related to user profile management")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "Get current user's profile",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Successfully retrieved user profile"),
                    @ApiResponse(responseCode = "400",
                            description = "User not found",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = MessageDto.class))),
                    @ApiResponse(responseCode = "401",
                            description = "Unauthorized access - missing or invalid token")
            })
    public ResponseEntity<UserDto> getUser(
            @AuthenticationPrincipal
            UserDetailsImpl userDetails) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.getUserByPhoneNumber(userDetails.getUser().getPhoneNumber())
                );
    }

    @PostMapping
    @Operation(summary = "Update current user's profile",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "Profile successfully updated"),
                    @ApiResponse(responseCode = "400",
                            description = "Validation error or password mismatch",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = MessageDto.class))),
                    @ApiResponse(responseCode = "401",
                            description = "Unauthorized access - missing or invalid token"),
            })
    public ResponseEntity<UpdateUserDto> updateUser(
            @Valid
            @RequestBody
            UpdateUserDto updateUserDto,
            @AuthenticationPrincipal
            UserDetailsImpl userDetails) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.updateUser(updateUserDto, userDetails.getUser().getPhoneNumber())
                );
    }

    @PostMapping("/token")
    @Operation(summary = "Update or create current user's device token")
    @ApiResponse(responseCode = "200",
            description = "Device token successfully updated")
    @ApiResponse(responseCode = "400",
            description = "Bad Request - Invalid input data",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = @ExampleObject(value = "{ \"timestamp\": \"2025-05-16T19:09:03.280+00:00\"," +
                            " \"status\": 400," +
                            " \"error\": \"Bad Request\"," +
                            " \"path\": \"/api/v1/profile/token\" }")))
    @ApiResponse(responseCode = "401",
            description = "Unauthorized access - missing or invalid access token")
    public ResponseEntity<Void> updateDeviceToken(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody @Valid DeviceTokenDto deviceTokenDto) {
        userService.updateDeviceToken(deviceTokenDto.getDeviceToken(),
                userDetails.getUser().getId());
        return ResponseEntity.ok().build();
    }
}
