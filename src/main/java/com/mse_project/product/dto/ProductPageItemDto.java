package com.mse_project.product.dto;

import com.mse_project.product.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductPageItemDto {
    private Long productId;
    private String productName;
    private String productCode;
    private String currentVersion;
    private Integer stockQuantity;
    private Integer safetyStock;

    public static ProductPageItemDto from(Product product) {
        return ProductPageItemDto.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .productCode(product.getProductCode())
                .currentVersion(product.getCurrentVersion())
                .stockQuantity(product.getStockQuantity())
                .safetyStock(product.getSafetyStock())
                .build();
    }
}
