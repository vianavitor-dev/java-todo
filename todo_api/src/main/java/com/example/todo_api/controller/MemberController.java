package com.example.todo_api.controller;

import com.example.todo_api.dao.GroupDao;
import com.example.todo_api.dao.MemberDao;
import com.example.todo_api.dao.UserDao;
import com.example.todo_api.exceptions.ResourceNotFoundException;
import com.example.todo_api.model.Group;
import com.example.todo_api.model.Member;
import com.example.todo_api.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/members")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class MemberController {
    @Autowired
    private MemberDao dao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private GroupDao groupDao;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Member>> getById(@PathVariable Long id) {
        Member member = dao.get(id)
                .orElseThrow(() -> new ResourceNotFoundException("Membro não encontrado"));

        return new ResponseEntity<>(
                new ApiResponse<>(false, "", member),
                HttpStatus.OK
        );
    }

    /*
    Endpoint: GET: http://localhost:8080/members/, http://localhost:8080/members?group-id=<...>

    Está função retorna tanto todos os membros registrados no banco de dados,
    quanto os membros presentes em determinado grupo
    */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Member>>> listMembers(
            @RequestParam(name = "group-id", required = false) Long  groupId
    ) {
        List<Member> data; // variável resposável por receber os resultados das pesquisas

        /*
        Se existir o parametro "groupId" o "data" recebera os membros registrados no grupo
        baseado no "groupId"
        */
        if (groupId != null && groupId > 0) {
            data = dao.getMembersInGroup(groupId);
        } else {
            data = dao.getAll();
        }

        return new ResponseEntity<>(
                new ApiResponse<>(false, "", data),
                HttpStatus.OK
        );
    }

    /*
    Endpoint: POST: http://localhost:8080/members

    Está função registra um novo membro. Para isso ele busca e associa as 2 entidades relacionadas
    a ele, grupo e usuário
    */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Member member) {
        if (member == null || member.getUser() == null || member.getGroup() == null) {
                throw new NullPointerException("Member fields or the member itself are null");
        }

        // Buscando usuário e grupo com base nos Ids provideciados pelo Member.user/group
        User user = userDao.get(member.getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        Group group = groupDao.get(member.getGroup().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Grupo não encontrado"));

        // Associa os usuários e grupos encontrados com o membro (novo registro) antes de salvar os dados
        member.setUser(user);
        member.setGroup(group);
        dao.save(member);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        dao.delete(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping
    public ResponseEntity<?> update(@RequestBody Member member) {
        dao.update(member);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
