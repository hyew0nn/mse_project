package com.mse_project.common.exception.business;

import com.mse_project.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class AuthBusinessExceptions {
    public static class UserDetailNotFoundException extends BusinessException {
        public UserDetailNotFoundException() {
            super(
                    "USER_DETAIL_NOT_FOUND ",
                    "인증된 사용자의 상세 정보가 존재하지 않습니다.",
                    HttpStatus.NOT_FOUND
            );
        }
    }
}
