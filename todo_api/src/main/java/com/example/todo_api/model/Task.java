package com.example.todo_api.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Entity
@Table(name = "task")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
    private String description;

    @Column(name = "create_at")
    private LocalDate createAt;

    @Column(name = "update_at")
    private LocalDate updateAt;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member creator;

    @Enumerated(EnumType.STRING)
    private Status status;

    public Task() {
        this.setCreateAt();
        this.setUpdateAt();
        this.Todo();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Member getCreator() {
        return creator;
    }

    public void setCreator(Member creator) {
        this.creator = creator;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public boolean isTodo() {
        return this.status.compare("todo");
    }

    public boolean isInProgress() {
        return this.status.compare("in-progress");
    }

    public boolean isDone() {
        return this.status.compare("done");
    }

    public void Todo() {
        this.status = Status.TODO;
    }

    public void InProgress() {
        this.status = Status.IN_PROGRESS;
    }

    public void Done() {
        this.status = Status.DONE;
    }

    public LocalDate getCreateAt() {
        return createAt;
    }

    private void setCreateAt() {
        Instant now = Instant.now();
        ZoneId zoneId = ZoneId.of("America/Sao_Paulo");

        this.createAt = ZonedDateTime.ofInstant(now, zoneId).toLocalDate();
    }

    public LocalDate getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt() {
        Instant now = Instant.now();
        ZoneId zoneId = ZoneId.of("America/Sao_Paulo");

        this.updateAt = ZonedDateTime.ofInstant(now, zoneId).toLocalDate();
    }
}