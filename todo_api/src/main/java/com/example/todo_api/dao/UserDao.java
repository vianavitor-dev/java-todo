package com.example.todo_api.dao;

import com.example.todo_api.model.User;
import jakarta.persistence.NoResultException;

public interface UserDao extends Dao<User, Long> {
    User verify(User data) throws NoResultException;
}
