package com.example.todo_api.controller;

import com.example.todo_api.exceptions.ResourceNotFoundException;
import com.example.todo_api.implement.MemberDaoImpl;
import com.example.todo_api.implement.TaskDaoImpl;
import com.example.todo_api.model.Member;
import com.example.todo_api.model.Task;
import jakarta.persistence.NoResultException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/tasks")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class TaskController {
    @Autowired
    private TaskDaoImpl dao;

    @Autowired
    private MemberDaoImpl memberDao;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Task>> getById(@PathVariable Long id) {
        Task group = dao.get(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada"));

        return new ResponseEntity<>(
                new ApiResponse<>(false, "", group),
                HttpStatus.OK
        );
    }

    /*
    Endpoint: GET: http://localhost:8080/tasks, http://localhost:8080/tasks?group-id=<...>

    Está função lista todos as tarefas registradas, e as presentes em determinado grupo
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Task>>> listTasks(
            @RequestParam(name = "group-id", required = false) Long groupId
    ) {
        List<Task> task;

        if (groupId != null && groupId > 0) {
            task = dao.getTasksOfGroup(groupId);
        } else {
            task =  dao.getAll();
        }

        return new ResponseEntity<>(
                new ApiResponse<>(false, "", task),
                HttpStatus.OK
        );
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Task task) {
        if (task == null || task.getCreator() == null) {
            throw new NullPointerException("Task fields or the task itself are null");
        }

        // Buscando dados do membro que está criando a tarefa para associa-lo a tarefa
        Member member = memberDao.get(task.getCreator().getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Membro não encontrado"));

        task.setCreator(member);
        dao.save(task);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping
    public ResponseEntity<?> update(@RequestBody Task task) {
        dao.update(task);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        dao.delete(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
