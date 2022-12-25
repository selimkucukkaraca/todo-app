package com.demo.todoapp.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserUpdateRequest {

    @NotBlank
    private String username;
    @NotBlank
    private String password;
    private String imageUrl;


}
