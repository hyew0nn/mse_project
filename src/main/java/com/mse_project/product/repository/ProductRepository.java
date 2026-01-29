package com.mse_project.product.repository;

import com.mse_project.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Boolean existsByProductCode(String productCode);
    Optional<Product> findByProductId(Long productId);
}
