package com.mse_project.common.exception.business;

import com.mse_project.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class OrderBusinessExceptions {

    public static class OrderNotFoundByOrderIdException extends BusinessException {
        public OrderNotFoundByOrderIdException(Long orderId) {
            super(
                    "ORDER_NOT_FOUND ",
                    "해당 주문을 찾을 수 없습니다.(" + orderId + ")",
                    HttpStatus.NOT_FOUND
            );
        }
    }
}
