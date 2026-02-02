package com.mse_project.product.service;

import com.mse_project.admin.dto.AdminSessionDto;
import com.mse_project.product.dto.DetailProductResponse;
import com.mse_project.product.dto.InsertProductRequest;
import com.mse_project.product.dto.PageProductResponse;
import com.mse_project.product.dto.UpdateProductRequest;
import org.springframework.data.domain.Pageable;

public interface ProductService {
    void insertProduct(AdminSessionDto admin, InsertProductRequest request);
    void updateProduct(AdminSessionDto admin, UpdateProductRequest request, Long productId);
    void deleteProduct(AdminSessionDto admin, Long productId);
    DetailProductResponse getProductDetail(Long productId);
    PageProductResponse getProductPage(Pageable pageable);
}
