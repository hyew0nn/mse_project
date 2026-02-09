package com.mse_project.order.dto;

import com.mse_project.order.entity.enums.OrderStatus;
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
    private OrderStatus orderStatus;
    private LocalDateTime startedAt;
    private LocalDateTime deadline;
}
