package ru.itis.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import ru.itis.api.dto.MessageDto;
import ru.itis.api.dto.UpdateUserDto;
import ru.itis.api.dto.UserDto;
import ru.itis.api.exception.PasswordDoNotMatchException;
import ru.itis.api.exception.UserAlreadyExistException;
import ru.itis.api.exception.UserNotFoundException;
import ru.itis.api.security.details.UserDetailsImpl;
import ru.itis.api.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/profile")
@Tag(name = "User Profile API",
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
                            content = @Content(mediaType = "application/json",
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
                            content = @Content(mediaType = "application/json",
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


    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<MessageDto> handleUserAlreadyExistException(
            UserAlreadyExistException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new MessageDto().setMessage(e.getMessage())
                );
    }

    @ExceptionHandler(PasswordDoNotMatchException.class)
    public ResponseEntity<MessageDto> handlePasswordDoNotMatchException(PasswordDoNotMatchException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new MessageDto().setMessage(e.getMessage())
                );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MessageDto> handleValidationExceptions(MethodArgumentNotValidException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new MessageDto()
                        .setMessage(
                                e.getAllErrors()
                                        .get(0)
                                        .getDefaultMessage()
                        )
                );
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<MessageDto> handleUserNotFoundException(UserNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new MessageDto().setMessage(e.getMessage())
                );
    }
}
