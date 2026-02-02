package com.mse_project.auth.dto;

import com.mse_project.admin.entity.Admin;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {

    private String adminName;
    private String department;
    private String position;

    public static LoginResponse from(Admin admin) {
        return new LoginResponse(
                admin.getAdminName(),
                admin.getDepartment().name(),
                admin.getPosition().name()
        );
    }
}
