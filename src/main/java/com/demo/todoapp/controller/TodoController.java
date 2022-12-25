package com.demo.todoapp.controller;

import com.demo.todoapp.dto.TodoDto;
import com.demo.todoapp.request.TodoCreateRequest;
import com.demo.todoapp.service.TodoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/todo")
@CrossOrigin
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @PostMapping
    public ResponseEntity<TodoDto> save(@Valid @RequestBody TodoCreateRequest request){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(todoService.save(request));
    }

    @DeleteMapping
    public ResponseEntity<?> delete(@RequestParam String publicId){
        todoService.deleteByPublicId(publicId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{mail}")
    public ResponseEntity<List<TodoDto>> getByUser(@PathVariable String mail){
        return ResponseEntity
                .ok(todoService.getByUser(mail));
    }

    @PutMapping("/update-done-status")
    public ResponseEntity<?> getTodoByPublicId(@RequestParam(value = "publicId") String publicId, @RequestParam(value = "status") boolean status){
        todoService.updateTodoDoneStatus(publicId, status);
        return ResponseEntity
                .noContent()
                .build();
    }

    @PostMapping("/clone")
    public ResponseEntity<TodoDto> cloneTodoByPublicId(@RequestParam String publicId){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(todoService.cloneTodoByPublicId(publicId));
    }
}