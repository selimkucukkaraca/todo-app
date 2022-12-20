package com.demo.todoapp.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Random;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ConfirmCode extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private int code = new Random().nextInt(1000);
}
