package com.mse_project.product.dto;

import com.mse_project.product.entity.Product;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductDto {
    private Long productId;
    private String productName;
    private String productCode;
    private String currentVersion;

    public static ProductDto toEntity(Product product) {
        return ProductDto.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .productCode(product.getProductCode())
                .currentVersion(product.getCurrentVersion())
                .build();
    }
}
