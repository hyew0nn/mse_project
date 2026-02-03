package com.mse_project.admin.dto;

import com.mse_project.admin.entity.Admin;
import com.mse_project.admin.entity.enums.AdminDepartment;
import com.mse_project.admin.entity.enums.AdminPosition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AdminDto {
    private Long adminId;
    private String adminCode;
    private String adminName;
    private AdminDepartment department;
    private AdminPosition position;

    public static AdminDto ofEntity(Admin admin) {
        return AdminDto.builder()
                .adminId(admin.getAdminId())
                .adminCode(admin.getAdminCode())
                .adminName(admin.getAdminName())
                .department(admin.getDepartment())
                .position(admin.getPosition())
                .build();
    }
}
