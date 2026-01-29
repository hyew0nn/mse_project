package com.mse_project.product.controller;

import com.mse_project.admin.entity.Admin;
import com.mse_project.common.ApiResponse;
import com.mse_project.common.annotation.CurrentAdmin;
import com.mse_project.product.dto.InsertProductRequest;
import com.mse_project.product.dto.UpdateProductRequest;
import com.mse_project.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping("/insert")
    public ResponseEntity<ApiResponse<String>> insertProduct(
            @CurrentAdmin Admin admin,
            @RequestBody InsertProductRequest request){

        productService.insertProduct(admin, request);
        return ResponseEntity.ok(ApiResponse.success("상품 추가에 성공하셨습니다."));
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse<String>> updateProduct(
            @CurrentAdmin Admin admin,
            @RequestBody UpdateProductRequest request){

        productService.updateProduct(admin, request);
        return ResponseEntity.ok(ApiResponse.success("상품 수정에 성공하셨습니다."));
    }

    @DeleteMapping("/update/{productId}")
    public ResponseEntity<ApiResponse<String>> deleteProduct(
            @CurrentAdmin Admin admin,
            @PathVariable("productId") Long productId){

        productService.deleteProduct(admin, productId);
        return ResponseEntity.ok(ApiResponse.success("상품 삭제에 성공하셨습니다."));
    }
}
