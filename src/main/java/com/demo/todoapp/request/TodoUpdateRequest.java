package com.demo.todoapp.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TodoUpdateRequest {

    @NotBlank
    private String body;
    @NotBlank
    private String title;
    private String imageUrl;
}
