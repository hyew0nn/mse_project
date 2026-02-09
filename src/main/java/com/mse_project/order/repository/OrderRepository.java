package com.mse_project.order.repository;

import com.mse_project.order.dto.DetailOrderResponse;
import com.mse_project.order.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT new com.mse_project.order.dto.DetailOrderResponse(" +
            "o.orderId, o.quantity, o.status, o.startedAt, o.deadline," +
            "new com.mse_project.product.dto.ProductDto(" +
            "p.productId, p.productName, p.productCode, p.currentVersion), " +
            "new com.mse_project.order.dto.CustomerDto(" +
            "c.customerId, c.customerName, c.customerType, c.customerPhone, c.customerEmail), " +
            "new com.mse_project.admin.dto.AdminDto(" +
            "a.adminId, a.adminCode, a.adminName, a.department, a.position)) " +
            "FROM Order o " +
            "JOIN o.product p " +
            "JOIN o.customer c " +
            "JOIN Admin a ON o.updatedBy = a.adminId " +
            "WHERE o.orderId = :orderId"
    ) Optional<DetailOrderResponse> getProductDetailDto(@Param("orderId") Long orderId);

    @EntityGraph(attributePaths = {"product"})
    @Query("SELECT o FROM Order o")
    Page<Order> findAllWithProduct(Pageable pageable);
}
