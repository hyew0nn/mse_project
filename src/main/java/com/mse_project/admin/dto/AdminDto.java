package com.mse_project.admin.dto;

import com.mse_project.admin.entity.enums.AdminDepartment;
import com.mse_project.admin.entity.enums.AdminPosition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDto {
    private Long adminId;
    private String adminCode;
    private String adminName;
    private String department;
    private String position;

    public AdminDto(Long adminId, String adminCode, String adminName,
                    AdminDepartment department, AdminPosition position) {
        this.adminId = adminId;
        this.adminCode = adminCode;
        this.adminName = adminName;
        this.department = department.name();
        this.position = position.name();
    }
}
