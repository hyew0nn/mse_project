package com.mse_project.common.exception;

import com.mse_project.common.ApiResponse;
import com.mse_project.common.BaseCode;
import jakarta.transaction.SystemException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException exception) {
        log.warn("Exception: ", exception);

        return ResponseEntity
                .status(exception.getStatus())
                .body(ApiResponse.error(exception));
    }

    @ExceptionHandler(SystemException.class)
    public ResponseEntity<ApiResponse<Void>> handleSystemException(SystemException exception) {
        log.error("System Exception: ", exception);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(
                        BaseCode.INTERNAL_SERVER_ERROR.getCode(),
                        BaseCode.INTERNAL_SERVER_ERROR.getStatus(),
                        BaseCode.INTERNAL_SERVER_ERROR.getMessage()
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<String>> handleValidationException(MethodArgumentNotValidException exception) {
        log.warn("Validation Exception: ", exception);

        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(
                        BaseCode.INVALID_REQUEST.getCode(),
                        BaseCode.INVALID_REQUEST.getStatus(),
                        message));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(
            MethodArgumentTypeMismatchException exception) {

        log.warn("Type Mismatch: parameter={}, value={}, requiredType={}",
                exception.getName(), exception.getValue(), exception.getRequiredType());

        String message = String.format("'%s' 파라미터의 값이 올바르지 않습니다: %s",
                exception.getName(), exception.getValue());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(
                        BaseCode.INVALID_REQUEST.getCode(),
                        BaseCode.INVALID_REQUEST.getStatus(),
                        message
                ));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadable(
            HttpMessageNotReadableException exception) {

        log.warn("Message Not Readable: ", exception);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(
                        BaseCode.INVALID_REQUEST.getCode(),
                        BaseCode.INVALID_REQUEST.getStatus(),
                        "요청 본문을 읽을 수 없습니다"
                ));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentials(BadCredentialsException exception) {
        log.warn("Bad Credentials: ", exception);

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(
                        BaseCode.ADMIN_LOGIN_FAIL.getCode(),
                        BaseCode.ADMIN_LOGIN_FAIL.getStatus(),
                        BaseCode.ADMIN_LOGIN_FAIL.getMessage()
                ));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException exception) {
        log.warn("Access Denied: ", exception);

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(
                        BaseCode.FORBIDDEN.getCode(),
                        BaseCode.FORBIDDEN.getStatus(),
                        BaseCode.FORBIDDEN.getMessage()
                ));
    }

    @ExceptionHandler({
            DataAccessResourceFailureException.class
    })
    public ResponseEntity<ApiResponse<Void>> handleRedisFailure(Exception exception) {
        log.error("Redis Connection Failure: ", exception);

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponse.error(
                        BaseCode.DATABASE_UNAVAILABLE.getCode(),
                        BaseCode.DATABASE_UNAVAILABLE.getStatus(),
                        BaseCode.DATABASE_UNAVAILABLE.getMessage()
                ));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataAccessException(DataAccessException exception) {
        log.error("Data Access Exception: ", exception);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(
                        BaseCode.DATABASE_UNAVAILABLE.getCode(),
                        BaseCode.DATABASE_UNAVAILABLE.getStatus(),
                        "데이터베이스 작업 중 오류가 발생했습니다"
                ));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolation(
            DataIntegrityViolationException exception) {

        log.warn("Data Integrity Violation: ", exception);

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(
                        "DATA_INTEGRITY_VIOLATION",
                        HttpStatus.CONFLICT,
                        "데이터 무결성 제약 조건을 위반했습니다"
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception exception) {
        log.error("Unhandled Exception: ", exception);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(
                        BaseCode.INTERNAL_SERVER_ERROR.getCode(),
                        BaseCode.INTERNAL_SERVER_ERROR.getStatus(),
                        "예상치 못한 오류가 발생했습니다"
                ));
    }
}