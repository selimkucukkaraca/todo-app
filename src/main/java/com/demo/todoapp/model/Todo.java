package com.demo.todoapp.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Todo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String publicId = UUID.randomUUID().toString();
    private String title;
    private String body;
    @ManyToOne
    private User user;

    public Todo(String title, String body, User user) {
        this.title = title;
        this.body = body;
        this.user = user;
    }
}
