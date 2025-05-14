package com.social.horror_pool.exception;

import com.social.horror_pool.payload.APIExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDate;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(APIException.class)
    public ResponseEntity<APIExceptionResponse> handleApiException(APIException e) {
        APIExceptionResponse apiExceptionResponse = new APIExceptionResponse(e.getMessage(), LocalDate.now());
        return new ResponseEntity<APIExceptionResponse>(apiExceptionResponse,HttpStatus.BAD_REQUEST);
    }
}
