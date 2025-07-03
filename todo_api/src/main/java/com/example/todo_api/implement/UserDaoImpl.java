package com.example.todo_api.implement;

import com.example.todo_api.dao.Dao;
import com.example.todo_api.exceptions.ResourceNotFoundException;
import com.example.todo_api.model.Group;
import com.example.todo_api.model.Member;
import com.example.todo_api.model.Task;
import com.example.todo_api.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserDaoImpl implements Dao<User, Long> {
    @Autowired
    private EntityManager em;

    @Override
    public List<User> getAll() {
        TypedQuery<User> query = em.createQuery("SELECT e FROM User e", User.class);
        return query.getResultList();
    }

    @Override
    public Optional<User> get(Long id) {
        if (id == null) {
            throw new NullPointerException("User id is null");
        }

        return Optional.ofNullable(em.find(User.class, id));
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void save(User data) {
        if (data == null) {
            throw new NullPointerException("User data cannot be null");
        }

        try {
            em.persist(data);
        }
        catch (Exception e) {
            System.out.println("Save Exception: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void delete(Long id) throws ResourceNotFoundException {
        if (id == null) {
            throw new NullPointerException("User id is null");
        }

        User user = this.get(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        // Buscando membros que a entidade usuário está relacionada
        TypedQuery<Member> memberQuery = em.createQuery(
                "SELECT m FROM Member m WHERE m.user.id = :id",
                Member.class
        ).setParameter("id", id);

        // Buscando grupos que o usuário criou
        TypedQuery<Group> groupQuery = em.createQuery(
                "SELECT g FROM Group g WHERE g.creator.id = :id",
                Group.class
        ).setParameter("id", id);

        // Buscando tarefas criadas pelo membro associado ao usuário
        TypedQuery<Task> taskQuery = em.createQuery(
                "SELECT t FROM Task t JOIN t.creator m WHERE m.user.id = :id",
                Task.class
        ).setParameter("id", id);

        try {
            /*
            Modificando o criador do grupo para nulo, prevenindo
            problemas na remoção do usuário do banco de dados
             */
            groupQuery.getResultList().forEach((group) -> {
                group.setCreator(null);
                em.merge(group);
            });

            /*
             Modificando o criador da tarefa para nulo, prevenindo
             problemas na remoção do membro do banco de dados
             */
            taskQuery.getResultList().forEach((task) -> {
                task.setCreator(null);
                em.merge(task);
            });

            // Removendo membros que que a entidade usuário estava associada
            memberQuery.getResultList().forEach((member) -> {
                em.remove(member);
            });

            em.remove(user);
        } catch (Exception e) {
            throw new RuntimeException("Delete Exception: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void update(User data) {
        if (data == null) {
            throw new NullPointerException("User data cannot be null");
        }

        try {
            em.merge(data);
        } catch (Exception e) {
            throw new RuntimeException("Update Exception: " + e.getMessage());
        }
    }

    /*
    Está função verifica se o usuário está registrado no sistema com base
    no email e senha
     */
    public User verify(User data) throws NoResultException {
        TypedQuery<User> query = em.createQuery(
                "SELECT u FROM User u WHERE u.username = :username AND u.password = :password",
                User.class
        );

        query.setParameter("username", data.getUsername());
        query.setParameter("password", data.getPassword());

        return query.getSingleResult();
    }
}
