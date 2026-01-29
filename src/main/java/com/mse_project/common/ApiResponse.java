package com.mse_project.common;

import com.mse_project.common.exception.BusinessException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
public class ApiResponse<T> {

    private final String code;
    private final HttpStatus status;
    private final String message;
    private final T data;
    private final LocalDateTime timestamp;

    // 성공용 생성자
    private ApiResponse(String code, HttpStatus status, String message, T data) {
        this.code = code;
        this.status = status;
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    // 실패용 생성자
    private ApiResponse(BusinessException exception, T data) {
        this.code = exception.getErrorCode();
        this.status = exception.getStatus();
        this.message = exception.getMessage();
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>("SUCCESS", HttpStatus.OK, message, data);
    }

    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>("SUCCESS", HttpStatus.OK, message, null);
    }

    public static <T> ApiResponse<T> error(BusinessException exception) {
        return new ApiResponse<>(exception, null);
    }

    public static <T> ApiResponse<T> error(String code, HttpStatus status, String message) {
        return new ApiResponse<>(code, status, message, null);
    }

}