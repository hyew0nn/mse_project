package com.mse_project.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class InsertOrderRequest {
    private Long productId;
    private Long customerId;
    private String productVersion;
    private Integer quantity;
    private String orderStatus;
    private LocalDateTime startedAt;
    private LocalDateTime deadline;
}
