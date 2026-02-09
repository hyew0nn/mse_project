package com.mse_project.product.dto;

import com.mse_project.admin.dto.AdminDto;
import com.mse_project.product.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class DetailProductResponse {
    private Long productId;
    private String productName;
    private String productCode;
    private String currentVersion;
    private String productDescription;
    private Integer stockQuantity;
    private Integer safetyStock;
    private AdminDto adminInfo;

    public static DetailProductResponse of(Product product, AdminDto adminDto) {
        return DetailProductResponse.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .productCode(product.getProductCode())
                .currentVersion(product.getCurrentVersion())
                .productDescription(product.getProductDescription())
                .stockQuantity(product.getStockQuantity())
                .safetyStock(product.getSafetyStock())
                .adminInfo(adminDto)
                .build();
    }
}
