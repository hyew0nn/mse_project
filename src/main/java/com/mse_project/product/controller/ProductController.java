package com.mse_project.product.controller;

import com.mse_project.admin.dto.AdminSessionDto;
import com.mse_project.common.ApiResponse;
import com.mse_project.common.annotation.CurrentAdmin;
import com.mse_project.product.dto.DetailProductResponse;
import com.mse_project.product.dto.InsertProductRequest;
import com.mse_project.product.dto.PageProductResponse;
import com.mse_project.product.dto.UpdateProductRequest;
import com.mse_project.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageProductResponse>> getProductPage(
            @PageableDefault(
                    size = 10,
                    sort = "createDate",
                    direction = Sort.Direction.DESC
            ) Pageable pageable
            ){

        PageProductResponse response = productService.getProductPage(pageable);
        return ResponseEntity.ok(ApiResponse.success("상품 페이지 조회에 성공하셨습니다.", response));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<DetailProductResponse>> getProductDetail(
            @PathVariable("productId") Long productId){

        DetailProductResponse response = productService.getProductDetail(productId);
        return ResponseEntity.ok(ApiResponse.success("상품 조회에 성공하셨습니다.", response));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<String>> insertProduct(
            @CurrentAdmin AdminSessionDto admin,
            @Valid @RequestBody InsertProductRequest request){

        productService.insertProduct(admin, request);
        return ResponseEntity.ok(ApiResponse.success("상품 추가에 성공하셨습니다."));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ApiResponse<String>> updateProduct(
            @CurrentAdmin AdminSessionDto admin,
            @RequestBody UpdateProductRequest request,
            @Valid @PathVariable("productId") Long productId){

        productService.updateProduct(admin, request, productId);
        return ResponseEntity.ok(ApiResponse.success("상품 수정에 성공하셨습니다."));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponse<String>> deleteProduct(
            @CurrentAdmin AdminSessionDto admin,
            @PathVariable("productId") Long productId){

        productService.deleteProduct(admin, productId);
        return ResponseEntity.ok(ApiResponse.success("상품 삭제에 성공하셨습니다."));
    }
}
