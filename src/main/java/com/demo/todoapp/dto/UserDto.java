package com.demo.todoapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
public class UserDto {
    private String username;
    private String mail;
    private boolean isActive;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
}
