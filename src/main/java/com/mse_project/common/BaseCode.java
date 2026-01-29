package com.mse_project.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BaseCode {
    // 성공
    SUCCESS("SUCCESS", HttpStatus.OK, "성공"),

    // 클라이언트 에러
    INVALID_REQUEST("INVALID_REQUEST", HttpStatus.BAD_REQUEST, "잘못된 요청입니다"),
    UNAUTHORIZED("UNAUTHORIZED", HttpStatus.UNAUTHORIZED, "인증이 필요합니다"),
    FORBIDDEN("FORBIDDEN", HttpStatus.FORBIDDEN, "권한이 없습니다"),

    // 서버 에러
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다"),
    SERVICE_UNAVAILABLE("SERVICE_UNAVAILABLE", HttpStatus.SERVICE_UNAVAILABLE, "서비스를 사용할 수 없습니다"),

    // 인증 관련
    ADMIN_LOGIN_FAIL("USER_LOGIN_FAIL", HttpStatus.UNAUTHORIZED, "로그인에 실패했습니다"),

    // 외부 서비스
    REDIS_UNAVAILABLE("REDIS_UNAVAILABLE", HttpStatus.SERVICE_UNAVAILABLE, "Redis 연결에 실패했습니다"),
    DATABASE_UNAVAILABLE("DATABASE_UNAVAILABLE", HttpStatus.SERVICE_UNAVAILABLE, "데이터베이스 연결에 실패했습니다");

    private final String code;
    private final HttpStatus status;
    private final String message;
}