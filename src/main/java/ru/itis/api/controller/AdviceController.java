package ru.itis.api.controller;


import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.itis.api.dto.MessageDto;
import ru.itis.api.exception.*;

@Hidden
@RestControllerAdvice
public class AdviceController {

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<MessageDto> handleUserAlreadyExistException(
            UserAlreadyExistException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new MessageDto().setMessage(e.getMessage()));
    }

    @ExceptionHandler(PasswordDoNotMatchException.class)
    public ResponseEntity<MessageDto> handleUserAlreadyExistException(
            PasswordDoNotMatchException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new MessageDto().setMessage(e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<MessageDto> handleValidationExceptions(
            MethodArgumentNotValidException e) {
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

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<MessageDto> handleNotFoundException(NotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new MessageDto().setMessage(e.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<MessageDto> handleAccessDeniedException(AccessDeniedException e) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new MessageDto().setMessage(e.getMessage()));
    }

    @ExceptionHandler(OperationNotAllowedForOwnerException.class)
    public ResponseEntity<MessageDto> handleOperationNotAllowedForOwnerException(OperationNotAllowedForOwnerException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new MessageDto().setMessage(e.getMessage()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<MessageDto> handleUserNotFoundException(UserNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new MessageDto().setMessage(e.getMessage())
                );
    }
}
