package com.mse_project.product.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UpdateProductRequest {
    @NotBlank(message = "상품명은 필수입니다.")
    @Size(min = 2, message = "상품명은 최소 2자 이상이어야 합니다.")
    private String productName;

    @NotBlank(message = "상품코드는 필수입니다.")
    @Size(min = 3, message = "상품코드는 최소 3자 이상이어야 합니다.")
    private String productCode;

    @Pattern(regexp="^\\\\d{1,2}.\\\\d{1,2}$")
    private String currentVersion;

    private String productDescription;
    private Integer stockQuantity;
    private Integer safetyStock;
}
