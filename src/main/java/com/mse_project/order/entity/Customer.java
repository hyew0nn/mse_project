package com.mse_project.order.entity;

import com.mse_project.common.BaseTimeEntity;
import com.mse_project.order.entity.enums.CustomerType;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "customers")
public class Customer extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(name = "customer_phone")
    private String customerPhone;

    @Enumerated(EnumType.STRING)
    @Column(name = "customer_type", nullable = false)
    private CustomerType customerType; // 개인, 주식회사, 중견기업, 대기업

    @Column(name = "customer_email")
    private String customerEmail;

    @Column(name = "updated_by")
    private Long updatedBy; // 상태 변경 관리자 ID

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Order> orders = new ArrayList<>();
}