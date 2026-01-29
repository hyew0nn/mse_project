package com.mse_project.admin.entity;

import com.mse_project.admin.dto.CreateAdminRequest;
import com.mse_project.admin.entity.enums.AdminDepartment;
import com.mse_project.admin.entity.enums.AdminPosition;
import com.mse_project.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "admins",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "admin_code")
        },
        indexes = {
                @Index(name = "idx_admin_code", columnList = "admin_code")
        }
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Admin extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id")
    private Long adminId;

    @Column(name = "admin_code", nullable = false, length = 50, unique = true)
    private String adminCode;

    @Column(name = "admin_name", nullable = false, length = 50)
    private String adminName;

    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "department", nullable = false, length = 30)
    private AdminDepartment department;

    @Enumerated(EnumType.STRING)
    @Column(name = "position", nullable = false, length = 30)
    private AdminPosition position;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    public void updateLastLoginAt(LocalDateTime lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public static Admin toEntity(CreateAdminRequest request, String password) {
        return Admin.builder()
                .adminCode(request.getAdminCode())
                .adminName(request.getAdminName())
                .password(password)
                .department(AdminDepartment.valueOf(request.getDepartment()))
                .position(AdminPosition.valueOf(request.getPosition()))
                .build();
    }
}
