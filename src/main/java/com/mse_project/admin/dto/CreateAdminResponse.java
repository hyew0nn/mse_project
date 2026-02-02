package com.mse_project.admin.dto;

import com.mse_project.admin.entity.Admin;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateAdminResponse {
    private Long adminId;
    private String adminCode;
    private String adminName;
    private String department;
    private String position;
    private String createdAt;

    public static CreateAdminResponse from(Admin admin) {
        return new CreateAdminResponse(
                admin.getAdminId(),
                admin.getAdminCode(),
                admin.getAdminName(),
                admin.getDepartment().name(),
                admin.getPosition().name(),
                admin.getCreateDate().toString()
        );
    }
}
