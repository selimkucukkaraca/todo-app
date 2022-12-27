package com.demo.todoapp.service;

import com.demo.todoapp.dto.TodoDto;
import com.demo.todoapp.dto.UserDto;
import com.demo.todoapp.exception.NotFoundException;
import com.demo.todoapp.exception.UserNotActiveException;
import com.demo.todoapp.model.Todo;
import com.demo.todoapp.model.User;
import com.demo.todoapp.repository.TodoRepository;
import com.demo.todoapp.request.TodoCreateRequest;
import com.demo.todoapp.request.TodoUpdateRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TodoService {

    private final TodoRepository todoRepository;
    private final UserService userService;

    public TodoService(TodoRepository todoRepository,
                       UserService userService) {
        this.todoRepository = todoRepository;
        this.userService = userService;
    }


    public TodoDto convertTodoToTodoDto(Todo from) {
        return new TodoDto(
                from.getPublicId(),
                from.getTitle(),
                from.getBody(),
                from.getCreateDate(),
                from.getUpdateDate(),
                from.getImageUrl(),
                from.isDone(),
                from.getCompletionDate(),
                userService.convertUserToUserDto(from.getUser())
        );
    }

    public TodoDto getByPublicId(String publicId) {
        Todo fromDbTodo = todoRepository.findTodoByPublicId(publicId)
                .orElseThrow(() -> new NotFoundException("publicId not found :" + publicId));

        return convertTodoToTodoDto(fromDbTodo);
    }


    public TodoDto cloneTodoByPublicId(String publicId) {
        var fromDbTodo = getTodoByPublicId(publicId);

        var cloneTodo = new Todo(
                fromDbTodo.getTitle(),
                fromDbTodo.getBody(),
                fromDbTodo.getUser(),
                fromDbTodo.getImageUrl(),
                fromDbTodo.getCompletionDate()
        );

        todoRepository.save(cloneTodo);
        return convertTodoToTodoDto(cloneTodo);
    }


    public TodoDto save(TodoCreateRequest request) {
        User user = userService.getUserByMail(request.getUserMail());

        var saved = new Todo(
                request.getTitle(),
                request.getBody(),
                user,
                request.getImageUrl(),
                request.getCompletionDate()
        );

        if (!user.isActive()) {
            throw new UserNotActiveException("user not active, mail: " + saved.getUser().getMail());
        }
        todoRepository.save(saved);
        return convertTodoToTodoDto(saved);
    }

    public void deleteByPublicId(String publicId) {
        var fromTodo = getTodoByPublicId(publicId);
        todoRepository.delete(fromTodo);
    }

    public List<TodoDto> getByUser(String mail) {
        var user = userService.getUserByMail(mail);
        return todoRepository.findTodoByUser(user)
                .stream()
                .map(this::convertTodoToTodoDto)
                .collect(Collectors.toList());
    }

    public void updateTodoDoneStatus(String publicId, boolean status) {
        var fromDbTodo = getTodoByPublicId(publicId);
        fromDbTodo.setDone(status);
        todoRepository.save(fromDbTodo);
    }

    public TodoDto updateTodo(String publicId, Optional<TodoUpdateRequest> request) {
        var fromDbTodo = getTodoByPublicId(publicId);

        fromDbTodo.setBody(request.get().getBody());
        fromDbTodo.setTitle(request.get().getTitle());
        fromDbTodo.setImageUrl(request.get().getImageUrl());
        todoRepository.save(fromDbTodo); //TODO

        return convertTodoToTodoDto(fromDbTodo);
    }

    protected Todo getTodoByPublicId(String publicId) {
        return todoRepository.findTodoByPublicId(publicId)
                .orElseThrow(() -> new NotFoundException(""));
    }


}
