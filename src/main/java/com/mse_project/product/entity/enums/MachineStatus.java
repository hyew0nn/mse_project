package com.mse_project.product.entity.enums;

import lombok.Getter;

@Getter
public enum MachineStatus {
    ON("가동"),
    OFF("정지"),
    FAULT("고장"),
    FIX("수리중");

    private final String description;

    MachineStatus(String description) {
        this.description = description;
    }

}