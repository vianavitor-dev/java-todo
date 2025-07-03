package com.example.todo_api.dao;

import com.example.todo_api.model.Task;

import java.util.List;

public interface TaskDao extends Dao<Task, Long> {
    List<Task> getTasksOfGroup(Long groupId);
}
