package com.mse_project.admin.service;

import com.mse_project.admin.dto.CreateAdminRequest;
import com.mse_project.admin.dto.CreateAdminResponse;

public interface AdminService {
    CreateAdminResponse registerAdmin(CreateAdminRequest request);
    void updateLastLoginAt(Long adminId);
}
