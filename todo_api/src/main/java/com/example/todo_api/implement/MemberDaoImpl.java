package com.example.todo_api.implement;

import com.example.todo_api.dao.Dao;
import com.example.todo_api.dao.MemberDao;
import com.example.todo_api.exceptions.ResourceNotFoundException;
import com.example.todo_api.model.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class MemberDaoImpl implements MemberDao {
    @Autowired
    private EntityManager em;

    @Override
    public List<Member> getAll() {
        TypedQuery<Member> query = em.createQuery("SELECT m FROM Member m", Member.class);
        return query.getResultList();
    }

    @Override
    public Optional<Member> get(Long id) {
        if (id == null) {
            throw new NullPointerException("Member id is null");
        }

        return Optional.ofNullable(em.find(Member.class, id));
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void save(Member data) {
        if (data == null) {
            throw new NullPointerException("Member data cannot be null");
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
            throw new NullPointerException("Member id is null");
        }

        Member member = this.get(id)
                .orElseThrow(() -> new ResourceNotFoundException("Membro não encontrado"));

        try {
            em.remove(member);
        } catch (Exception e) {
            throw new RuntimeException("Delete Exception: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void update(Member data) {
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
    Está função busca e retorna os membros registrados no grupo
     */
    @Override
    public List<Member> getMembersInGroup(Long groupId) {
        String select = "SELECT m FROM Member m JOIN m.group g WHERE g.id = :groupId";
        TypedQuery<Member> query = em.createQuery(select, Member.class);
        query.setParameter("groupId", groupId);

        return query.getResultList();
    }

}
