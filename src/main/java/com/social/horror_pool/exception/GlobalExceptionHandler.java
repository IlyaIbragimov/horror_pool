package com.social.horror_pool.exception;

import com.social.horror_pool.payload.APIExceptionResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(APIException.class)
    public ResponseEntity<APIExceptionResponse> handleApiException(APIException e, HttpServletRequest request) {
        APIExceptionResponse apiExceptionResponse = new APIExceptionResponse(
                e.getMessage(),
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI(),
                null
        );
        return new ResponseEntity<APIExceptionResponse>(apiExceptionResponse,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<APIExceptionResponse> handleResourceNotFoundException(ResourceNotFoundException e, HttpServletRequest request) {
        APIExceptionResponse apiExceptionResponse = new APIExceptionResponse(
                e.getMessage(),
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                request.getRequestURI(),
                null
        );
        return new ResponseEntity<APIExceptionResponse>(apiExceptionResponse,HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIExceptionResponse> handleDTOValidationException(MethodArgumentNotValidException e, HttpServletRequest request) {
        List<String> errors = new ArrayList<>();

        e.getBindingResult().getFieldErrors().forEach(error -> {
            if (!errors.contains(error.getDefaultMessage())) {
                errors.add(error.getDefaultMessage());
            }
        });

        return buildValidationErrorResponse(request, errors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<APIExceptionResponse> handleConstraintViolationException(ConstraintViolationException e, HttpServletRequest request) {
        List<String> errors = e.getConstraintViolations().stream()
                .map(violation -> violation.getMessage())
                .distinct()
                .toList();

        return buildValidationErrorResponse(request, errors);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<APIExceptionResponse> handleHandlerMethodValidationException(HandlerMethodValidationException e, HttpServletRequest request) {
        List<String> errors = new ArrayList<>();

        e.getParameterValidationResults().forEach(result ->
                result.getResolvableErrors().forEach(error -> addValidationError(errors, error))
        );

        e.getCrossParameterValidationResults().forEach(error -> addValidationError(errors, error));

        if (errors.isEmpty()) {
            errors.add(e.getMessage());
        }

        return buildValidationErrorResponse(request, errors);
    }

    private void addValidationError(List<String> errors, MessageSourceResolvable error) {
        String message = error.getDefaultMessage();
        if (message != null && !errors.contains(message)) {
            errors.add(message);
        }
    }

    private ResponseEntity<APIExceptionResponse> buildValidationErrorResponse(HttpServletRequest request, List<String> errors) {
        APIExceptionResponse apiExceptionResponse = new APIExceptionResponse(
                "Validation failed",
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI(),
                errors
        );

        return new ResponseEntity<APIExceptionResponse>(apiExceptionResponse, HttpStatus.BAD_REQUEST);
    }
}
