package com.mse_project.admin.entity.enums;

import lombok.Getter;

@Getter
public enum AdminPosition {
    STAFF("사원"),
    ASSISTANT("주임"),
    MANAGER("대리"),
    SENIOR_MANAGER("과장"),
    DIRECTOR("부장"),
    EXECUTIVE("임원");

    private final String description;

    AdminPosition(String description) {
        this.description = description;
    }

}
