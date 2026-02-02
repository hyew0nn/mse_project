package com.mse_project.product.service;

import com.mse_project.admin.entity.Admin;
import com.mse_project.product.dto.InsertProductRequest;
import com.mse_project.product.dto.UpdateProductRequest;
import com.mse_project.product.entity.Product;

public interface ProductService {
    void insertProduct(Admin admin, InsertProductRequest request);
    void updateProduct(Admin admin, UpdateProductRequest request);
    void deleteProduct(Admin admin, Long productId);
    void getProductDetail(Product product);
    void getListProduct();
}
