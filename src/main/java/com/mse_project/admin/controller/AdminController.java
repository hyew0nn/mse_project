package com.mse_project.admin.controller;

import com.mse_project.admin.dto.CreateAdminRequest;
import com.mse_project.admin.dto.CreateAdminResponse;
import com.mse_project.admin.service.AdminService;
import com.mse_project.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final AdminService adminService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<CreateAdminResponse>> createAdmin(
            @RequestBody CreateAdminRequest request) {
        CreateAdminResponse response = adminService.registerAdmin(request);

        return ResponseEntity.ok(ApiResponse.success("관리자 등록 성공", response));
    }
}
