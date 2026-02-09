package com.mse_project.order.dto;

import com.mse_project.order.entity.enums.OrderStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class InsertOrderRequest {
    @NotNull
    private Long productId;

    @NotNull
    private Long customerId;

    @NotNull
    private String productVersion;

    @Min(value = 1, message = "수량은 1 이하일 수 없습니다.")
    private Integer quantity;

    private OrderStatus orderStatus;
    private LocalDateTime startedAt;
    private LocalDateTime deadline;

}
