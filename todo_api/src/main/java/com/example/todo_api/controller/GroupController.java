package com.example.todo_api.controller;

import com.example.todo_api.exceptions.ResourceNotFoundException;
import com.example.todo_api.implement.GroupDaoImpl;
import com.example.todo_api.implement.MemberDaoImpl;
import com.example.todo_api.implement.UserDaoImpl;
import com.example.todo_api.model.Group;
import com.example.todo_api.model.Member;
import com.example.todo_api.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/groups")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class GroupController {
    @Autowired
    private GroupDaoImpl dao;

    @Autowired
    private UserDaoImpl userDao;

    @Autowired
    private MemberDaoImpl memberDao;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Group>> getById(@PathVariable Long id) {
        Group group = dao.get(id)
                .orElseThrow(() -> new ResourceNotFoundException("Grupo não encontrado"));

        return new ResponseEntity<>(
                new ApiResponse<>(false, "", group),
                HttpStatus.OK
        );
    }

    /*
    Endpoint: GET: http://localhost:8080/groups?user-id=<...>, http://localhost:8080/groups

    Está função é resposável por retornar todos os grupos registrados e os
    grupos que determinado usuário pertence
    */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Group>>> listGroup(
            @RequestParam(name = "user-id", required = false) Long userId
    ) {
        List<Group> data;

        if (userId != null) {
            data = dao.getGroupsFromUser(userId);
        } else {
            data = dao.getAll();
        }

        return new ResponseEntity<>(
                new ApiResponse<>(false, "", data),
                HttpStatus.OK
        );
    }

    /*
    Endpoint: POST: http://localhost:8080/groups

    Está função é responsável por registrar um novo grupo no banco de dados,
    também registrando um novo membro (o usuário que criou o grupo) como seu criador
     */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Group group) {
        if (group == null || group.getCreator() == null) {
            throw new NullPointerException("Group parameter is null");
        }

        // Buscando o usuário que está criando o grupo
        User user = userDao.get(group.getCreator().getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        
        group.setCreator(user);
        dao.save(group);

        // Registrando o usuário como membro e criador do próprio grupo
        Member member = new Member();
        member.setUser(user);
        member.setGroup(group);
        member.setRegularUser();
        member.setGroupCreator(true);

        memberDao.save(member);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        dao.delete(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping
    public ResponseEntity<?> update(@RequestBody Group data) {
        dao.update(data);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
