package com.mse_project.product.service.impl;

import com.mse_project.admin.dto.AdminSessionDto;
import com.mse_project.common.exception.business.ProductBusinessExceptions;
import com.mse_project.product.dto.DetailProductResponse;
import com.mse_project.product.dto.InsertProductRequest;
import com.mse_project.product.dto.PageProductResponse;
import com.mse_project.product.dto.UpdateProductRequest;
import com.mse_project.product.entity.Product;
import com.mse_project.product.repository.ProductRepository;
import com.mse_project.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    @Transactional
    @Override
    public void insertProduct(AdminSessionDto admin, InsertProductRequest request) {
        Boolean existProduct = productRepository.existsByProductCode(request.getProductCode());

        if (existProduct) {
            throw new ProductBusinessExceptions.ProductAlreadyExistException();
        }

        Product product = Product.toEntity(request, admin.adminId());
        productRepository.save(product);
    }

    @Transactional
    @Override
    public void updateProduct(AdminSessionDto admin, UpdateProductRequest request, Long productId) {
        Product product = getProduct(productId);
        product.updateProduct(admin.adminId(), request);
    }

    @Transactional
    @Override
    public void deleteProduct(AdminSessionDto admin, Long productId) {
        Product product = getProduct(productId);
        product.deleteProduct(admin.adminId());
    }

    @Override
    public DetailProductResponse getProductDetail(Long productId) {
        return productRepository.getProductDetailDto(productId)
                .orElseThrow(() -> new ProductBusinessExceptions.ProductNotFoundByProductIdException(productId));
    }

    @Override
    public PageProductResponse getProductPage(Pageable pageable) {
        Page<Product> page = productRepository.findAll(pageable);
        return PageProductResponse.from(page);
    }

    @Override
    public Product getProduct(Long productId) {
        return productRepository.findByProductId(productId)
                .orElseThrow(() -> new ProductBusinessExceptions.ProductNotFoundByProductIdException(productId));

    }

}
