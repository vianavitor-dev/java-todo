package com.example.todo_api.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "member")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    @Column(nullable = false, name = "is_group_creator")
    private boolean isGroupCreator = false;
    /*
     0 -> "regular-user": it can create, delete, and update any tasks, whatever they belong to him or not;
     1 -> "guest": it can only view the tasks
    */
    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false)
    private Type type = Type.GUEST;
    //private Type type;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isGroupCreator() {
        return isGroupCreator;
    }

    public void setGroupCreator(boolean value) {
        this.isGroupCreator = value;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Type getType() {
        return type;
    }

    public boolean isGuest() {
        return this.type.compare(1);
    }

    public boolean isRegularUser() {
        return this.type.compare(2);
    }

    public void setGuest() {
        this.type = Type.GUEST;
    }

    public void setRegularUser() {
        this.type = Type.REGULAR;
    }

}
