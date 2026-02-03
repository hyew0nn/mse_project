package com.mse_project.order.entity;

import com.mse_project.common.BaseTimeEntity;
import com.mse_project.order.dto.InsertOrderRequest;
import com.mse_project.order.entity.enums.OrderStatus;
import com.mse_project.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "orders")
public class Order extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status; // 접수, 진행중, 완료, 지연

    @Column(name = "estimated_completion_at")
    private LocalDateTime estimatedCompletionAt; // 주문완료 예측 일시

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "deadline")
    private LocalDateTime deadline;

    @Column(name = "updated_by")
    private Long updatedBy;

    public static Order toEntity(Long adminId, Customer customer, Product product, InsertOrderRequest request) {
        return Order.builder()
                .customer(customer)
                .product(product)
                .quantity(request.getQuantity())
                .status(OrderStatus.valueOf(request.getOrderStatus()))
                .startedAt(request.getStartedAt())
                .deadline(request.getDeadline())
                .updatedBy(adminId)
                .build();
    }
}