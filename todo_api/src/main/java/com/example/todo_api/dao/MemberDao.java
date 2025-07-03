package com.example.todo_api.dao;

import com.example.todo_api.model.Member;

import java.util.List;

public interface MemberDao extends Dao<Member, Long> {
    List<Member> getMembersInGroup(Long groupId);
}
