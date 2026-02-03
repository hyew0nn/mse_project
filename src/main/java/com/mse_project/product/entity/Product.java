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

    @Column(name = "stock_quantity", nullable = false)
    @Builder.Default
    private Integer stockQuantity = 0;

    @Column(name = "safety_stock", nullable = false)
    @Builder.Default
    private Integer safetyStock = 0;

    @Column(name = "product_description", columnDefinition = "TEXT")
    private String productDescription;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    @Column(name = "updated_by")
    private Long updatedBy;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProductPart> productParts = new ArrayList<>();

    public void updateProduct(Long adminId, UpdateProductRequest request) {
        if (request.getProductName() != null) {
            this.productName = request.getProductName();}

        if (request.getProductCode() != null) {
            this.productCode = request.getProductCode();}

        if (request.getCurrentVersion() != null) {
            this.currentVersion = request.getCurrentVersion();}

        if (request.getProductDescription() != null) {
            this.productDescription = request.getProductDescription();}

        if (request.getStockQuantity() != null) {
            this.stockQuantity = request.getStockQuantity();}

        if (request.getSafetyStock() != null) {
            this.safetyStock = request.getSafetyStock();}

        this.updatedBy = adminId;
    }

    public void decreaseStock(int quantity) {
        if (this.stockQuantity < quantity) {
            throw new IllegalStateException("재고가 부족합니다.");
        }
        this.stockQuantity -= quantity;
    }


    public void deleteProduct(Long adminId) {
        this.isDeleted = true;
        this.updatedBy = adminId;
    }

    public static Product toEntity(InsertProductRequest insertProductRequest, Long adminId) {
        return Product.builder()
                .productCode(insertProductRequest.getProductCode())
                .productName(insertProductRequest.getProductName())
                .currentVersion(insertProductRequest.getCurrentVersion())
                .productDescription(insertProductRequest.getProductDescription())
                .stockQuantity(insertProductRequest.getStockQuantity())
                .safetyStock(insertProductRequest.getSafetyStock())
                .updatedBy(adminId)
                .build();
    }
}