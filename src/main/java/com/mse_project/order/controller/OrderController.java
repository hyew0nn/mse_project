package com.mse_project.order.controller;

import com.mse_project.admin.dto.AdminSessionDto;
import com.mse_project.common.ApiResponse;
import com.mse_project.common.annotation.CurrentAdmin;
import com.mse_project.order.dto.DetailOrderResponse;
import com.mse_project.order.dto.InsertOrderRequest;
import com.mse_project.order.dto.PageOrderResponse;
import com.mse_project.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<DetailOrderResponse>> getOrderDetail(
            @PathVariable("orderId") Long orderId){
        DetailOrderResponse response = orderService.getOrderDetail(orderId);

        return ResponseEntity.ok(ApiResponse.success("주문 상세 조회에 성공하셨습니다.", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageOrderResponse>> getOrderPage(
            @PageableDefault(
                    size = 10,
                    sort = "createDate",
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    ){
        PageOrderResponse response = orderService.getOrdersPage(pageable);
        return ResponseEntity.ok(ApiResponse.success("주문 페이지 조회에 성공하셨습니다.", response));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<String>> createOrder(
            @CurrentAdmin AdminSessionDto admin,
            @RequestBody InsertOrderRequest request
    ){
        orderService.createOrder(admin, request);
        return ResponseEntity.ok(ApiResponse.success("주문 생성에 성공하셨습니다."));
    }
}
