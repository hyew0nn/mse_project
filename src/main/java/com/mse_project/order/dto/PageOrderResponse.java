package com.mse_project.order.dto;

import com.mse_project.order.entity.Order;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Builder
public class PageOrderResponse {
    private List<OrderPageItemDto> orderPageItems;
    private Long orderCount;
    private Integer currentPage;
    private Integer totalPage;

    public static PageOrderResponse from(Page<Order> page) {
        return PageOrderResponse.builder()
                .orderPageItems(
                        page.getContent().stream()
                                .map(OrderPageItemDto::from)
                                .toList()
                )
                .orderCount(page.getTotalElements())
                .currentPage(page.getNumber())
                .totalPage(page.getTotalPages())
                .build();
    }

}
