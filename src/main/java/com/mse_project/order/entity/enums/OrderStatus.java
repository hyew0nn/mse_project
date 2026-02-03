package com.mse_project.order.entity.enums;

import lombok.Getter;

@Getter
public enum OrderStatus {
    RECEIVED("접수"),
    IN_PROGRESS("진행중"),
    COMPLETED("완료"),
    DELAYED("지연");

    private final String description;
    OrderStatus(String description) { this.description = description; }
}