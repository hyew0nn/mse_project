package com.mse_project.common.exception.business;

import com.mse_project.common.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class ProductBusinessExceptions {

    public static class ProductNotFoundByProductIdException extends BusinessException {
        public ProductNotFoundByProductIdException(Long productId) {
            super(
                    "PRODUCT_NOT_FOUND ",
                    "해당 상품을 찾을 수 없습니다.(" + productId + ")",
                    HttpStatus.NOT_FOUND
            );
        }
    }

    public static class ProductAlreadyExistException extends BusinessException {
        public ProductAlreadyExistException() {
            super(
                    "PRODUCT_ALREADY_EXIST ",
                    "이미 존재하는 상품입니다.",
                    HttpStatus.ALREADY_REPORTED
            );
        }
    }

}
