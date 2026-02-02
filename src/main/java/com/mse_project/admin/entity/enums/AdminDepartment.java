package com.mse_project.admin.entity.enums;

import lombok.Getter;

@Getter
public enum AdminDepartment {
    OPERATION("영업지원"),
    CUSTOMER("구매"),
    FACILITY("시설"),
    CONTENT("마케팅"),
    DEVELOPMENT("개발"),
    ADMIN("관리");

    private final String description;

    AdminDepartment(String description) {
        this.description = description;
    }

}
