package com.example.todo_api.model;

public enum Status {
    TODO("todo"), IN_PROGRESS("in-progress"), DONE("done");

    private final String value;

    Status(String value) {
        this.value = value;
    }

    public boolean compare(String value) {
        return value.equals(this.value);
    }

    @Override
    public String toString() {
        return this.value;
    }
}
