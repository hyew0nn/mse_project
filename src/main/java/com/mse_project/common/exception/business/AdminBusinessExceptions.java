package com.mse_project.common.exception.business;

import com.mse_project.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class AdminBusinessExceptions {

    public static class AdminNotFoundException extends BusinessException {
        public AdminNotFoundException() {
            super(
                    "ADMIN_NOT_FOUND",
                    "해당 관리자를 찾을 수 없습니다.",
                    HttpStatus.NOT_FOUND
            );
        }
    }

    public static class AdminAlreadyExistException extends BusinessException {
        public AdminAlreadyExistException() {
            super(
                    "ADMIN_ALREADY_EXIST",
                    "이미 존재하는 관리자입니다.",
                    HttpStatus.ALREADY_REPORTED
            );
        }
    }

}
