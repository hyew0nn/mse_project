package com.mse_project.order.dto;

import com.mse_project.order.entity.Customer;
import com.mse_project.order.entity.enums.CustomerType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CustomerDto {
    private Long customerId;
    private String customerName;
    private CustomerType customerType;
    private String customerPhone;
    private String customerEmail;

    public static CustomerDto ofEntity(Customer customer) {
        return CustomerDto.builder()
                .customerId(customer.getCustomerId())
                .customerName(customer.getCustomerName())
                .customerType(customer.getCustomerType())
                .customerPhone(customer.getCustomerPhone())
                .customerEmail(customer.getCustomerEmail())
                .build();
    }
}
