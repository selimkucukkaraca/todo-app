package com.demo.todoapp.service;

import com.demo.todoapp.dto.TodoDto;
import com.demo.todoapp.dto.UserDto;
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


    public TodoDto save(TodoCreateRequest request){
        User user = userService.getUserByMail(request.getUserMail());

        var saved = new Todo(
                request.getTitle(),
                request.getBody(),
                user
        );

        if (!user.isActive()){
            throw new RuntimeException();
        }
        todoRepository.save(saved);


        return new TodoDto(
                saved.getPublicId(),
                saved.getTitle(),
                saved.getBody(),
                saved.getCreateDate(),
                saved.getUpdateDate(),
                new UserDto(
                        user.getUsername(),
                        user.getMail(),
                        user.isActive(),
                        user.getCreateDate(),
                        user.getUpdateDate()
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
                        new UserDto(
                                todo.getUser().getUsername(),
                                todo.getUser().getMail(),
                                todo.getUser().isActive(),
                                todo.getUser().getCreateDate(),
                                todo.getUser().getUpdateDate()
                        )
                ))
                .collect(Collectors.toList());
     }

     protected Todo getTodoByPublicId(String publicId){
        return todoRepository.findTodoByPublicId(publicId)
                .orElseThrow(RuntimeException::new);
     }

}
