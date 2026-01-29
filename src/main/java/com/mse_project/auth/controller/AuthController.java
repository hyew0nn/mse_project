package com.mse_project.auth.controller;

import com.mse_project.auth.dto.LoginRequest;
import com.mse_project.auth.dto.LoginResponse;
import com.mse_project.auth.service.AuthService;
import com.mse_project.common.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        LoginResponse response =
                authService.login(request.getAdminCode(), request.getPassword(), httpRequest);

        httpRequest.getSession(true);
        return ResponseEntity.ok(ApiResponse.success("로그인 성공", response));
    }
}
