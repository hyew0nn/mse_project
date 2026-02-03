package com.mse_project.order.dto;

import com.mse_project.order.entity.Order;
import com.mse_project.order.entity.enums.OrderStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class OrderPageItemDto {
    private Long orderId;
    private Integer quantity;
    private OrderStatus orderStatus;
    private LocalDateTime startedAt;
    private LocalDateTime deadline;

    private String productName;
    private String currentVersion;

    public static OrderPageItemDto from(Order order) {
        return OrderPageItemDto.builder()
                .orderId(order.getOrderId())
                .quantity(order.getQuantity())
                .orderStatus(order.getStatus())
                .startedAt(order.getStartedAt())
                .deadline(order.getDeadline())
                .productName(order.getProduct().getProductName())
                .currentVersion(order.getProduct().getCurrentVersion())
                .build();
    }
}
