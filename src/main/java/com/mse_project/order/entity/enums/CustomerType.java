package com.mse_project.order.entity.enums;

import lombok.Getter;

@Getter
public enum CustomerType {
    INDIVIDUAL("개인"),
    CORPORATION("주식회사"),
    MID_SIZED("중견기업"),
    LARGE("대기업");

    private final String description;
    CustomerType(String description) { this.description = description; }
}