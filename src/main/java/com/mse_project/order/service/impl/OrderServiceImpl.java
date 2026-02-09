package com.mse_project.order.service.impl;

import com.mse_project.admin.dto.AdminSessionDto;
import com.mse_project.common.exception.business.OrderBusinessExceptions;
import com.mse_project.order.dto.DetailOrderResponse;
import com.mse_project.order.dto.InsertOrderRequest;
import com.mse_project.order.dto.PageOrderResponse;
import com.mse_project.order.entity.Customer;
import com.mse_project.order.entity.Order;
import com.mse_project.order.repository.OrderRepository;
import com.mse_project.order.service.CustomerService;
import com.mse_project.order.service.OrderService;
import com.mse_project.product.entity.Product;
import com.mse_project.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService;
    private final CustomerService customerService;

    @Transactional
    @Override
    public synchronized void createOrder(AdminSessionDto admin, InsertOrderRequest request) {
        Product product = productService.getProduct(request.getProductId());
        Customer customer = customerService.getCustomer(request.getCustomerId());

        Order order = Order.toEntity(admin.adminId(), customer, product, request);
        orderRepository.save(order);

        product.decreaseStock(request.getQuantity());
    }

    @Override
    public DetailOrderResponse getOrderDetail(Long orderId) {
        return orderRepository.getProductDetailDto(orderId)
                        .orElseThrow(() -> new OrderBusinessExceptions.OrderNotFoundByOrderIdException(orderId));
    }

    @Override
    public PageOrderResponse getOrdersPage(Pageable pageable) {
        Page<Order> page = orderRepository.findAllWithProduct(pageable);
        return PageOrderResponse.from(page);
    }

}
