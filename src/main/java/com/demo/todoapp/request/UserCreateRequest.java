package com.demo.todoapp.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserCreateRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @Email
    private String mail;
}
