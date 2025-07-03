package com.example.todo_api.exceptions;

import com.example.todo_api.controller.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleResourceNotFoundException(
            ResourceNotFoundException e
    ) {
        return new ResponseEntity<>(
                new ApiResponse<>(true, e.getMessage(), null),
                HttpStatus.NOT_FOUND
        );
    }
}
