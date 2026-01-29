package com.mse_project.admin.dto;

import lombok.Getter;

@Getter
public class CreateAdminRequest {
    private String adminCode;
    private String adminName;
    private String password;
    private String department;
    private String position;
}
