package com.example.todo_api.model;

public enum Type {
    GUEST(1), REGULAR(2);

    private final int accessLevel;

    Type(int accessLevel) {
        this.accessLevel = accessLevel;
    }

    public boolean compare(int accessLevel) {
        return this.accessLevel == accessLevel;
    }

    public int getAccessLevel() {
        return accessLevel;
    }
}
