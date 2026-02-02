package com.mse_project.admin.service.impl;

import com.mse_project.admin.dto.CreateAdminRequest;
import com.mse_project.admin.dto.CreateAdminResponse;
import com.mse_project.admin.entity.Admin;
import com.mse_project.admin.repository.AdminRepository;
import com.mse_project.admin.service.AdminService;
import com.mse_project.common.exception.business.AdminBusinessExceptions;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public CreateAdminResponse registerAdmin(CreateAdminRequest request) {
        Optional<Admin> adminExist = adminRepository.findByAdminCode(request.getAdminCode());

        if(adminExist.isPresent()) {
            throw new AdminBusinessExceptions.AdminAlreadyExistException();
        }

        Admin admin = createAdmin(request);
        return CreateAdminResponse.from(admin);
    }

    private Admin createAdmin(CreateAdminRequest request) {
        String password = passwordEncoder.encode(request.getPassword());

        Admin admin = Admin.toEntity(request, password);
        adminRepository.save(admin);

        return admin;
    }

    @Override
    @Transactional
    public void updateLastLoginAt(Admin admin) {
        admin.updateLastLoginAt(LocalDateTime.now());
    }
}
