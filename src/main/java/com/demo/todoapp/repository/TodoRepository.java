package com.demo.todoapp.repository;

import com.demo.todoapp.model.Todo;
import com.demo.todoapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo,Long> {

    Optional<Todo> findTodoByPublicId(String publicId);
    List<Todo> findTodoByUser(User user);

}
