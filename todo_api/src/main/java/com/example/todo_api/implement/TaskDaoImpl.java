package com.example.todo_api.implement;

import com.example.todo_api.dao.Dao;
import com.example.todo_api.exceptions.ResourceNotFoundException;
import com.example.todo_api.model.Task;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TaskDaoImpl implements Dao<Task, Long> {
    @Autowired
    private EntityManager em;

    @Override
    public List<Task> getAll() {
        TypedQuery<Task> query = em.createQuery("SELECT t FROM Task t", Task.class);
        return query.getResultList();
    }

    @Override
    public Optional<Task> get(Long id) {
        if (id == null) {
            throw new NullPointerException("Task id is null");
        }

        return Optional.ofNullable(em.find(Task.class, id));
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void save(Task data) {
        if (data == null) {
            throw new NullPointerException("Task data cannot be null");
        }

        try {
            em.persist(data);
        } catch (Exception e) {
            throw new RuntimeException("Save Exception: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void delete(Long id) throws ResourceNotFoundException {
        if (id == null) {
            throw new NullPointerException("Task id is null");
        }

        Task task = this.get(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada"));

        try {
            em.remove(task);
        } catch (Exception e) {
            throw new RuntimeException("Delete Exception: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void update(Task data) {
        if (data == null) {
            throw new NullPointerException("Task data cannot be null");
        }

        data.setUpdateAt();

        try {
            em.merge(data);
        } catch (Exception e) {
            throw new RuntimeException("Update Exception: " + e.getMessage());
        }
    }

    /*
    Está função busca e retorna as tarefas criadas em determinado grupo
     */
    public List<Task> getTasksOfGroup(Long groupId) {
        String select = "SELECT t FROM Task t JOIN t.creator c WHERE c.group.id = :groupId";
        TypedQuery<Task> query = em.createQuery(select, Task.class);
        query.setParameter("groupId", groupId);

        return query.getResultList();
    }
}
