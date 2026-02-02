package com.mse_project.product.dto;

import com.mse_project.product.entity.Product;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Builder
public class PageProductResponse {
    private List<ProductPageItemDto> productPageItems;
    private Long productCount;
    private Integer currentPage;
    private Integer totalPage;

    public static PageProductResponse from(Page<Product> page) {
        return PageProductResponse.builder()
                .productPageItems(
                        page.getContent().stream()
                                .map(ProductPageItemDto::from)
                                .toList()
                )
                .productCount(page.getTotalElements())
                .currentPage(page.getNumber())
                .totalPage(page.getTotalPages())
                .build();
    }

}
