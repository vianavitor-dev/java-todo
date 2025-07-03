package com.example.todo_api.dao;

import com.example.todo_api.model.Group;

import java.util.List;

public interface GroupDao extends Dao<Group, Long> {
    List<Group> getGroupsFromUser(Long userId);
}
