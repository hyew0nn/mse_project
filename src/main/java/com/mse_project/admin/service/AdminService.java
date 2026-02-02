package com.mse_project.admin.service;

import com.mse_project.admin.dto.CreateAdminRequest;
import com.mse_project.admin.dto.CreateAdminResponse;
import com.mse_project.admin.entity.Admin;

public interface AdminService {
    CreateAdminResponse registerAdmin(CreateAdminRequest request);
    void updateLastLoginAt(Admin admin);
}
