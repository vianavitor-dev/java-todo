package com.example.todo_api.exceptions;

/*
Exceção para quando uma busca no banco de dados não retorna nada
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
