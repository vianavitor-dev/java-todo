package com.example.todo_api.implement;

import com.example.todo_api.dao.Dao;
import com.example.todo_api.dao.GroupDao;
import com.example.todo_api.exceptions.ResourceNotFoundException;
import com.example.todo_api.model.Group;
import com.example.todo_api.model.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class GroupDaoImpl implements GroupDao {
    @Autowired
    private EntityManager em;

    @Override
    public List<Group> getAll() {
        TypedQuery<Group> query = em.createQuery("SELECT g FROM Group g", Group.class);
        return query.getResultList();
    }

    @Override
    public Optional<Group> get(Long id) {
        if (id == null) {
            throw new NullPointerException("Group id is null");
        }

        return Optional.ofNullable(em.find(Group.class, id));
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void save(Group data) {
        if (data == null) {
            throw new NullPointerException("Group data cannot be null");
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
            throw new NullPointerException("Group id is null");
        }

        Group group = this.get(id)
                .orElseThrow(() -> new ResourceNotFoundException("Grupo não encontrado"));

        // Buscando membros contidos no grupo
        TypedQuery<Member> query = em.createQuery(
                "SELECT m FROM Member m WHERE m.group.id =:id",
                Member.class
        ).setParameter("id", id);

        try {
            // Removendo os membros que pertecem ao grupo
            for (Member member : query.getResultList()) {
                em.remove(member);
            }

            em.remove(group);
        } catch (Exception e) {
            throw new RuntimeException("Delete Exception: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void update(Group data) {
        if (data == null) {
            throw new NullPointerException("Group data cannot be null");
        }

        try {
            em.merge(data);
        } catch (Exception e) {
            throw new RuntimeException("Update Exception: " + e.getMessage());
        }
    }

    /*
    Está função busca e retorna os grupos que o usuário está participando
     */
    @Override
    public List<Group> getGroupsFromUser(Long userId) {
        String select = "SELECT g FROM Group g JOIN Member m ON g.id = m.group.id WHERE m.user.id = :id";
        TypedQuery<Group> query = em.createQuery(select, Group.class);
        query.setParameter("id", userId);

        return query.getResultList();
    }
}
