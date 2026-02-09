package com.mse_project.product.repository;

import com.mse_project.product.dto.DetailProductResponse;
import com.mse_project.product.entity.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Boolean existsByProductCode(String productCode);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Product> findByProductId(Long productId);

    @Query("SELECT new com.mse_project.product.dto.DetailProductResponse(" +
            "p.productId, p.productName, p.productCode, p.currentVersion, p.productDescription, p.stockQuantity, p.safetyStock, " +
            "new com.mse_project.admin.dto.AdminDto(" +
            "a.adminId, a.adminCode, a.adminName, a.department, a.position)) " +
            "FROM Product p, Admin a " +
            "WHERE p.productId = :productId and p.updatedBy = a.adminId"
    ) Optional<DetailProductResponse> getProductDetailDto(@Param("productId") Long productId);
}
