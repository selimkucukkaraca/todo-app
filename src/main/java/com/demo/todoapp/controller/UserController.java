package com.demo.todoapp.controller;

import com.demo.todoapp.dto.UserDto;
import com.demo.todoapp.request.UserCreateRequest;
import com.demo.todoapp.request.UserLoginRequest;
import com.demo.todoapp.request.UserUpdateRequest;
import com.demo.todoapp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/user")
@CrossOrigin
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

    @PatchMapping("/deactive-user")
    public ResponseEntity<UserDto> deactiveUser(@RequestParam String mail){
        return ResponseEntity
                .ok(userService.deactivateUser(mail));
    }

    @GetMapping("/{mail}")
    public ResponseEntity<UserDto> getByMail(@PathVariable String mail){
        return ResponseEntity
                .ok(userService.getByMail(mail));
    }

    @DeleteMapping
    public ResponseEntity<?> delete(@RequestParam String mail){
        userService.delete(mail);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> login(@RequestBody UserLoginRequest request){
        return ResponseEntity
                .ok(userService.login(request));
    }

    @PutMapping("/update")
    public ResponseEntity<UserDto> updateUser(@RequestParam String mail,
                                              @RequestBody Optional<UserUpdateRequest> request){
        return ResponseEntity.ok(userService.updateUser(mail, request));
    }
}
