package com.mse_project.product.service.impl;

import com.mse_project.admin.entity.Admin;
import com.mse_project.common.exception.business.ProductBusinessExceptions;
import com.mse_project.product.dto.InsertProductRequest;
import com.mse_project.product.dto.UpdateProductRequest;
import com.mse_project.product.entity.Product;
import com.mse_project.product.repository.ProductRepository;
import com.mse_project.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    @Override
    public void insertProduct(Admin admin, InsertProductRequest request) {
        Boolean existProduct = productRepository.existsByProductCode(request.getProductCode());

        if (existProduct) {
            throw new ProductBusinessExceptions.ProductAlreadyExistException();
        }

        Product product = Product.toEntity(request, admin.getAdminCode());
        productRepository.save(product);
    }

    @Override
    public void updateProduct(Admin admin, UpdateProductRequest request) {
        Product product = getProduct(request.getProductId());

        product.updateProduct(admin.getAdminCode(), request);
        productRepository.save(product);
    }

    @Override
    public void deleteProduct(Admin admin, Long productId) {
        Product product = getProduct(productId);

        product.deleteProduct(admin.getAdminCode());
        productRepository.save(product);
    }

    @Override
    public void getProductDetail(Product product) {

    }

    @Override
    public void getListProduct() {

    }

    private Product getProduct(Long productId) {
        return productRepository.findByProductId(productId)
                .orElseThrow(() -> new ProductBusinessExceptions.ProductNotFoundByProductIdException(productId));

    }
}
