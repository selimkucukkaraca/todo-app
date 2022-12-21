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
}
