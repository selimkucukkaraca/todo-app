package com.demo.todoapp.service;

import com.demo.todoapp.dto.TodoDto;
import com.demo.todoapp.dto.UserDto;
import com.demo.todoapp.exception.NotFoundException;
import com.demo.todoapp.exception.generic.GenericExistException;
import com.demo.todoapp.model.Todo;
import com.demo.todoapp.model.User;
import com.demo.todoapp.repository.TodoRepository;
import com.demo.todoapp.request.TodoCreateRequest;
import org.springframework.stereotype.Service;

import java.util.List;
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


    public TodoDto cloneTodoByPublicId(String publicId){
        var fromDbTodo = getTodoByPublicId(publicId);

        var cloneTodo = new Todo(
                fromDbTodo.getTitle(),
                fromDbTodo.getBody(),
                fromDbTodo.getUser(),
                fromDbTodo.getImageUrl()
        );

        todoRepository.save(cloneTodo);

        return new TodoDto(
                cloneTodo.getPublicId(),
                cloneTodo.getTitle(),
                cloneTodo.getBody(),
                cloneTodo.getCreateDate(),
                cloneTodo.getUpdateDate(),
                cloneTodo.getImageUrl(),
                cloneTodo.isDone(),
                new UserDto(
                        cloneTodo.getUser().getUsername(),
                        cloneTodo.getUser().getMail(),
                        cloneTodo.getUser().isActive(),
                        cloneTodo.getUser().getCreateDate(),
                        cloneTodo.getUser().getUpdateDate(),
                        cloneTodo.getUser().getImageUrl(),
                        cloneTodo.getUser().getLastLoginDate()

                ));
    }


    public TodoDto save(TodoCreateRequest request){
        User user = userService.getUserByMail(request.getUserMail());

        var saved = new Todo(
                request.getTitle(),
                request.getBody(),
                user,
                request.getImageUrl()
        );

        if (!user.isActive()){
            throw new GenericExistException("");
        }
        todoRepository.save(saved);


        return new TodoDto(
                saved.getPublicId(),
                saved.getTitle(),
                saved.getBody(),
                saved.getCreateDate(),
                saved.getUpdateDate(),
                saved.getImageUrl(),
                saved.isDone(),
                new UserDto(
                        user.getUsername(),
                        user.getMail(),
                        user.isActive(),
                        user.getCreateDate(),
                        user.getUpdateDate(),
                        user.getImageUrl(),
                        user.getLastLoginDate()
                )
        );
    }

     public void deleteByPublicId(String publicId){
        var fromTodo = getTodoByPublicId(publicId);
        todoRepository.delete(fromTodo);
     }

     public List<TodoDto> getByUser(String mail){
        var user = userService.getUserByMail(mail);
        return todoRepository.findTodoByUser(user)
                .stream()
                .map(todo -> new TodoDto(
                        todo.getPublicId(),
                        todo.getTitle(),
                        todo.getBody(),
                        todo.getCreateDate(),
                        todo.getUpdateDate(),
                        todo.getImageUrl(),
                        todo.isDone(),
                        new UserDto(
                                todo.getUser().getUsername(),
                                todo.getUser().getMail(),
                                todo.getUser().isActive(),
                                todo.getUser().getCreateDate(),
                                todo.getUser().getUpdateDate(),
                                todo.getUser().getImageUrl(),
                                todo.getUser().getLastLoginDate()
                        )
                ))
                .collect(Collectors.toList());
     }

     public void updateTodoDoneStatus(String publicId, boolean status){
        var fromDbTodo = getTodoByPublicId(publicId);
        fromDbTodo.setDone(status);
        todoRepository.save(fromDbTodo);
     }

     protected Todo getTodoByPublicId(String publicId){
        return todoRepository.findTodoByPublicId(publicId)
                .orElseThrow(()->new NotFoundException(""));
     }


}
