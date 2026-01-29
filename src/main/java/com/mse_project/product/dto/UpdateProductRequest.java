package com.mse_project.product.dto;

import lombok.Getter;

@Getter
public class UpdateProductRequest {
    private Long productId;
    private String productName;
    private String productCode;
    private String currentVersion;
    private String productDescription;
}
