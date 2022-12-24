package com.demo.todoapp.service;

import com.demo.todoapp.dto.UserDto;
import com.demo.todoapp.exception.NotFoundException;
import com.demo.todoapp.exception.generic.GenericExistException;
import com.demo.todoapp.model.ConfirmCode;
import com.demo.todoapp.model.User;
import com.demo.todoapp.repository.ConfirmCodeRepository;
import com.demo.todoapp.repository.UserRepository;
import com.demo.todoapp.request.UserCreateRequest;
import com.demo.todoapp.request.UserLoginRequest;
import com.demo.todoapp.util.MailSendService;
import org.springframework.stereotype.Service;



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

    public UserDto save(UserCreateRequest request){
        var saved = new User(
                request.getUsername(),
                request.getPassword(),
                request.getMail(),
                request.getImageUrl()
        );

        if (userRepository.existsUserByMail(saved.getMail())){
            throw new GenericExistException("");
        }


        userRepository.save(saved);

        return new UserDto(
                saved.getUsername(),
                saved.getPassword(),
                saved.isActive(),
                saved.getCreateDate(),
                saved.getUpdateDate(),
                saved.getImageUrl()
        );
    }

    public void delete(String mail){
        var fromUser = getUserByMail(mail);
        userRepository.delete(fromUser);
    }

    public void sendConfirmCode(String mail){
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

    public UserDto activateUser(String mail, int code){
        var user = getUserByMail(mail);
        ConfirmCode confirmCode = confirmCodeRepository.findConfirmCodeByCode(code);

        if (user.getConfirmCode().getCode() == code) {
            user.setActive(true);
            confirmCodeRepository.deleteById(confirmCode.getId());
            userRepository.save(user);

            return new UserDto(
                    user.getUsername(),
                    user.getMail(),
                    user.isActive(),
                    user.getCreateDate(),
                    user.getUpdateDate(),
                    user.getImageUrl()
            );
        }
        return null;
    }

    public UserDto deactivateUser(String mail){
        var fromDbUser = getUserByMail(mail);
        fromDbUser.setActive(false);

        userRepository.save(fromDbUser);

        return new UserDto(
                fromDbUser.getUsername(),
                fromDbUser.getMail(),
                fromDbUser.isActive(),
                fromDbUser.getCreateDate(),
                fromDbUser.getUpdateDate(),
                fromDbUser.getImageUrl()
        );
    }

    public UserDto getByMail(String mail){
        User fromDbUser = userRepository.findUserByMail(mail)
                .orElseThrow();     // TODO

        return new UserDto(
                fromDbUser.getUsername(),
                fromDbUser.getMail(),
                fromDbUser.isActive(),
                fromDbUser.getCreateDate(),
                fromDbUser.getUpdateDate(),
                fromDbUser.getImageUrl()
        );
    }

    public UserDto login(UserLoginRequest request){
        var fromDbUser = getUserByMail(request.getMail());
        if (fromDbUser.getPassword().equals(request.getPassword())){
            return new UserDto(
                    fromDbUser.getUsername(),
                    fromDbUser.getMail(),
                    fromDbUser.isActive(),
                    fromDbUser.getCreateDate(),
                    fromDbUser.getUpdateDate(),
                    fromDbUser.getImageUrl()
            );
        }
        throw new RuntimeException(); //TODO
    }


    protected User getUserByMail(String mail){
        return userRepository.findUserByMail(mail)
                .orElseThrow(()->new NotFoundException(""));
    }

}
