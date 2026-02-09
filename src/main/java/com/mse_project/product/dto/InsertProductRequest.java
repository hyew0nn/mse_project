package com.mse_project.product.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;

@Getter
public class InsertProductRequest {
    @NotBlank(message = "상품명은 필수입니다.")
    @Size(min = 2, message = "상품명은 최소 2자 이상이어야 합니다.")
    private String productName;

    @NotBlank(message = "상품코드는 필수입니다.")
    @Size(min = 3, message = "상품코드는 최소 3자 이상이어야 합니다.")
    private String productCode;

    @NotNull
    @Pattern(
            regexp="^\\d{1,2}\\.\\d{1,2}$",
            message = "버전은 x.y 형식이어야 합니다"
    )
    private String currentVersion;

    private String productDescription;

    @Min(value = 0, message = "재고는 0 이하일 수 없습니다.")
    private Integer stockQuantity;

    @Min(value = 0, message = "안전 재고는 0 이하일 수 없습니다.")
    private Integer safetyStock;

    public int getStockQuantity() {
        return stockQuantity != null ? stockQuantity : 0;
    }

    public int getSafetyStock() {
        return safetyStock != null ? safetyStock : 0;
    }
}
