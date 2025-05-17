package com.social.horror_pool.exception;

import com.social.horror_pool.payload.APIExceptionResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(APIException.class)
    public ResponseEntity<APIExceptionResponse> handleApiException(APIException e, HttpServletRequest request) {
        APIExceptionResponse apiExceptionResponse = new APIExceptionResponse(
                e.getMessage(),
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                request.getRequestURI()
        );
        return new ResponseEntity<APIExceptionResponse>(apiExceptionResponse,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<APIExceptionResponse> handleResourceNotFoundException(ResourceNotFoundException e, HttpServletRequest request) {
        APIExceptionResponse apiExceptionResponse = new APIExceptionResponse(
                e.getMessage(),
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                request.getRequestURI()
        );
        return new ResponseEntity<APIExceptionResponse>(apiExceptionResponse,HttpStatus.NOT_FOUND);
    }
}
