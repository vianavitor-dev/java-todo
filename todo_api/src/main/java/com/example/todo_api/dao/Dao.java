package com.example.todo_api.dao;

import java.util.List;
import java.util.Optional;

public interface Dao<T, Id> {
    List<T> getAll();
    Optional<T> get(Id id);
    void save(T data);
    void delete(Id id);
    void update(T data);
}
