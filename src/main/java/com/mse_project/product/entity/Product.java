package com.mse_project.product.entity;

import com.mse_project.common.BaseTimeEntity;
import com.mse_project.product.dto.InsertProductRequest;
import com.mse_project.product.dto.UpdateProductRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Products")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "product_code", unique = true, nullable = false)
    private String productCode;

    @Column(name = "current_version")
    private String currentVersion;

    @Column(name = "product_description", columnDefinition = "TEXT")
    private String productDescription;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    @Column(name = "updated_by")
    private String updatedBy;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductPart> productParts = new ArrayList<>();

    public void updateProduct(String adminCode, UpdateProductRequest request) {
        this.productName = request.getProductName();
        this.productCode = request.getProductCode();
        this.currentVersion = request.getCurrentVersion();
        this.productDescription = request.getProductDescription();
        this.updatedBy = adminCode;
    }

    public void deleteProduct(String adminCode) {
        this.isDeleted = true;
        this.updatedBy = adminCode;
    }

    public static Product toEntity(InsertProductRequest insertProductRequest, String adminCode) {
        return Product.builder()
                .productCode(insertProductRequest.getProductCode())
                .productName(insertProductRequest.getProductName())
                .currentVersion(insertProductRequest.getCurrentVersion())
                .productDescription(insertProductRequest.getProductDescription())
                .updatedBy(adminCode)
                .build();
    }
}