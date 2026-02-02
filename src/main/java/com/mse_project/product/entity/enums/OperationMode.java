package com.mse_project.product.entity.enums;

import lombok.Getter;

@Getter
public enum OperationMode {
    MODE_24X7("24시간"),
    MODE_9TO18("9to18");

    private final String description;

    OperationMode(String description) {
        this.description = description;
    }

}
