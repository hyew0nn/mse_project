package com.mse_project.order.service.impl;

import com.mse_project.order.entity.Customer;
import com.mse_project.order.repository.CustomerRepository;
import com.mse_project.order.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;

    @Override
    public Customer getCustomer(Long customerId) {
        return customerRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }
}
