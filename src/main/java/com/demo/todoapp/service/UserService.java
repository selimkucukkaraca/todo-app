package com.demo.todoapp.service;

import com.demo.todoapp.dto.TodoDto;
import com.demo.todoapp.dto.UserDto;
import com.demo.todoapp.exception.NotFoundException;
import com.demo.todoapp.exception.generic.GenericExistException;
import com.demo.todoapp.model.ConfirmCode;
import com.demo.todoapp.model.Todo;
import com.demo.todoapp.model.User;
import com.demo.todoapp.repository.ConfirmCodeRepository;
import com.demo.todoapp.repository.UserRepository;
import com.demo.todoapp.request.UserCreateRequest;
import com.demo.todoapp.request.UserLoginRequest;
import com.demo.todoapp.request.UserUpdateRequest;
import com.demo.todoapp.util.MailSendService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;


@Service
public class UserService {

    private final UserRepository userRepository;
    private final MailSendService mailSendService;
    private final ConfirmCodeRepository confirmCodeRepository;

    public UserService(UserRepository userRepository,
                       MailSendService mailSendService,
                       ConfirmCodeRepository confirmCodeRepository) {
        this.userRepository = userRepository;
        this.mailSendService = mailSendService;
        this.confirmCodeRepository = confirmCodeRepository;
    }

    public UserDto convertUserToUserDto(User from) {
        return new UserDto(
                from.getUsername(),
                from.getMail(),
                from.isActive(),
                from.getCreateDate(),
                from.getUpdateDate(),
                from.getImageUrl(),
                from.getLastLoginDate()
        );
    }

    public UserDto save(UserCreateRequest request) {
        var saved = new User(
                request.getUsername(),
                request.getPassword(),
                request.getMail(),
                request.getImageUrl()
        );

        if (userRepository.existsUserByMail(saved.getMail())) {
            throw new GenericExistException("user already exist , mail : " + saved.getMail());
        }


        userRepository.save(saved);

        return convertUserToUserDto(saved);
    }

    public void delete(String mail) {
        var fromUser = getUserByMail(mail);
        userRepository.delete(fromUser);
    }

    public void sendConfirmCode(String mail) {
        var user = getUserByMail(mail);

        ConfirmCode confirmCode = new ConfirmCode();
        user.setConfirmCode(confirmCode);
        confirmCodeRepository.save(confirmCode);
        userRepository.save(user);

        mailSendService.sendMail(
                user.getMail(),
                "Your Verification Code",
                String.valueOf(confirmCode.getCode()));
    }

    public UserDto activateUser(String mail, int code) {
        var user = getUserByMail(mail);
        ConfirmCode confirmCode = confirmCodeRepository.findConfirmCodeByCode(code);

        if (user.getConfirmCode().getCode() == code) {
            user.setActive(true);
            confirmCodeRepository.deleteById(confirmCode.getId());
            userRepository.save(user);

            return convertUserToUserDto(user);
        }
        return null;
    }

    public UserDto deactivateUser(String mail) {
        var fromDbUser = getUserByMail(mail);
        fromDbUser.setActive(false);

        userRepository.save(fromDbUser);

        return convertUserToUserDto(fromDbUser);
    }

    public UserDto getByMail(String mail) {
        User fromDbUser = userRepository.findUserByMail(mail)
                .orElseThrow(() -> new NotFoundException("mail not found : " + mail));

        return convertUserToUserDto(fromDbUser);
    }

    public UserDto login(UserLoginRequest request) {
        var fromDbUser = getUserByMail(request.getMail());
        if (fromDbUser.getPassword().equals(request.getPassword())) {
            fromDbUser.setLastLoginDate(LocalDateTime.now());
            userRepository.save(fromDbUser);

            return convertUserToUserDto(fromDbUser);
        }
        throw new RuntimeException();
    }

    public UserDto updateUser(String mail, Optional<UserUpdateRequest> request) {
        var fromDbUser = getUserByMail(mail);
        fromDbUser.setUsername(request.get().getUsername());
        fromDbUser.setPassword(request.get().getPassword());
        fromDbUser.setImageUrl(request.get().getImageUrl());
        userRepository.save(fromDbUser);

        return convertUserToUserDto(fromDbUser);
    }

    protected User getUserByMail(String mail) {
        return userRepository.findUserByMail(mail)
                .orElseThrow(() -> new NotFoundException(""));
    }

}
