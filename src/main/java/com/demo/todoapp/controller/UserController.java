package com.demo.todoapp.controller;

import com.demo.todoapp.dto.UserDto;
import com.demo.todoapp.request.UserCreateRequest;
import com.demo.todoapp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class UserController{

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserDto> save(@Valid @RequestBody UserCreateRequest request){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.save(request));
    }

    @PostMapping("/send-confirm-code")
    public ResponseEntity<?> sendConfirmCode(@RequestParam String mail){
        userService.sendConfirmCode(mail);
        return ResponseEntity
                .noContent()
                .build();
    }

    @PatchMapping("/active-user")
    public ResponseEntity<UserDto> activateUser(@RequestParam String mail, @RequestParam int code){
        return ResponseEntity
                .ok(userService.activateUser(mail, code));
    }

}
