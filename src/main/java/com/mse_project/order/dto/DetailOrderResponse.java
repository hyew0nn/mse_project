package com.mse_project.order.dto;

import com.mse_project.admin.dto.AdminDto;
import com.mse_project.order.entity.Order;
import com.mse_project.order.entity.enums.OrderStatus;
import com.mse_project.product.dto.ProductDto;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class DetailOrderResponse {
    // 주문 정보
    private Long orderId;
    private Integer quantity;
    private OrderStatus orderStatus;
    private LocalDateTime startedAt;
    private LocalDateTime deadline;

    // 주문한 상품 정보
    private ProductDto productInfo;
    // 주문자
    private CustomerDto customerInfo;
    // 주문 생성자
    private AdminDto adminInfo;

    public static DetailOrderResponse of(
            Order order, ProductDto productDto, CustomerDto customerDto, AdminDto adminDto) {
        return DetailOrderResponse.builder()
                .orderId(order.getOrderId())
                .quantity(order.getQuantity())
                .orderStatus(order.getStatus())
                .startedAt(order.getStartedAt())
                .deadline(order.getDeadline())
                .productInfo(productDto)
                .customerInfo(customerDto)
                .adminInfo(adminDto)
                .build();
    }
}
