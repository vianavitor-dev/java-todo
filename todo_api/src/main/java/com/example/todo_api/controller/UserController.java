package com.example.todo_api.controller;

import com.example.todo_api.exceptions.ResourceNotFoundException;
import com.example.todo_api.implement.UserDaoImpl;
import com.example.todo_api.model.User;
import jakarta.persistence.NoResultException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class UserController {
    @Autowired
    private UserDaoImpl dao;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> getById(@PathVariable Long id) {
        User user = dao.get(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        return new ResponseEntity<>(
                new ApiResponse<>(false, "", user),
                HttpStatus.OK
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<User>>> listUsers() {
        return new ResponseEntity<>(
                new ApiResponse<>(false, "", dao.getAll()),
                HttpStatus.OK
        );
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody User user) {
        dao.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        dao.delete(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping
    public ResponseEntity<?> update(@RequestBody User user) {
        dao.update(user);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /*
    Endpoint: POST: http://localhost:8080/users/login

    Está função verifica se o usuário está registrado com base no email e senha
     */
    @PostMapping(value = "/login")
    public ResponseEntity<ApiResponse<User>> login(@RequestBody User data) {
        if (data == null) {
            throw new NullPointerException("User data cannot be null");
        }

        try {
            User user = dao.verify(data);

            return new ResponseEntity<>(
                    new ApiResponse<User>(false, "Login completado", user),
                    HttpStatus.OK
            );

            // Exceção que ocorre em casos onde a "query" não retorna resultado
        } catch (NoResultException e) {
            return new ResponseEntity<>(
                    new ApiResponse<>(true, "Email ou senha incorretos", null),
                    HttpStatus.NOT_FOUND
            );
        }
    }

}
