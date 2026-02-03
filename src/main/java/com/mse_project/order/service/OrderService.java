package com.mse_project.order.service;

import com.mse_project.admin.dto.AdminSessionDto;
import com.mse_project.order.dto.InsertOrderRequest;
import com.mse_project.order.dto.DetailOrderResponse;
import com.mse_project.order.dto.PageOrderResponse;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    void createOrder(AdminSessionDto admin, InsertOrderRequest request);
    DetailOrderResponse getOrderDetail(Long orderId);
    PageOrderResponse getOrdersPage(Pageable pageable);
}
