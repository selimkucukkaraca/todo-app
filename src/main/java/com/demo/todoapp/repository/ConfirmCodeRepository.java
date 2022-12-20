package com.demo.todoapp.repository;

import com.demo.todoapp.model.ConfirmCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfirmCodeRepository extends JpaRepository<ConfirmCode,Long> {

    ConfirmCode findConfirmCodeByCode(int code);

}
