package ru.drmemex.classifieds.common.exception.handler;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import ru.drmemex.classifieds.common.exception.ConflictException;
import ru.drmemex.classifieds.common.exception.ForbiddenException;
import ru.drmemex.classifieds.common.exception.NotFoundException;
import ru.drmemex.classifieds.common.exception.UnauthorizedException;
import ru.drmemex.classifieds.common.exception.ValidationException;
import ru.drmemex.classifieds.common.exception.response.ErrorResponse;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 400

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            ValidationException exception
    ) {
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                exception.getMessage()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception
    ) {

        String message = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse("Validation failed");

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                message
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(
            ConstraintViolationException exception
    ) {

        String message = exception.getConstraintViolations()
                .stream()
                .findFirst()
                .map(violation ->
                        violation.getPropertyPath() + ": "
                                + violation.getMessage()
                )
                .orElse("Validation failed");

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                message
        );
    }


    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException exception
    ) {

        String message = String.format(
                "Invalid value '%s' for parameter '%s'",
                exception.getValue(),
                exception.getName()
        );

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                message
        );
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFoundException(
            NoResourceFoundException exception
    ) {
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Invalid request path"
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException exception
    ) {
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Invalid request body"
        );
    }

    // 401

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(
            UnauthorizedException exception
    ) {
        return buildErrorResponse(
                HttpStatus.UNAUTHORIZED,
                exception.getMessage()
        );
    }

    // 403

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbiddenException(
            ForbiddenException exception
    ) {
        return buildErrorResponse(
                HttpStatus.FORBIDDEN,
                exception.getMessage()
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleSpringAccessDeniedException(
            AccessDeniedException exception
    ) {
        return buildErrorResponse(
                HttpStatus.FORBIDDEN,
                "Access Denied"
        );
    }

    // 404

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(
            NotFoundException exception
    ) {
        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                exception.getMessage()
        );
    }

    // 409

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflictException(
            ConflictException exception
    ) {
        return buildErrorResponse(
                HttpStatus.CONFLICT,
                exception.getMessage()
        );
    }

    // 500

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            Exception exception
    ) {
        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                exception.getMessage()
        );
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(
            HttpStatus status,
            String message
    ) {
        ErrorResponse response = new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                message,
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(status)
                .body(response);
    }
}
