package com.bistu.clubsystembackend.config;

import com.bistu.clubsystembackend.util.ApiResponse;
import com.bistu.clubsystembackend.util.BizCode;
import com.bistu.clubsystembackend.util.BusinessException;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Void> handleBusiness(BusinessException ex) {
        return ApiResponse.fail(ex.getCode(), ex.getMessage());
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            BindException.class,
            ConstraintViolationException.class,
            IllegalArgumentException.class
    })
    public ApiResponse<Void> handleValidation(Exception ex) {
        log.warn("参数校验失败: {}", ex.getMessage());
        return ApiResponse.fail(BizCode.PARAM_INVALID.getCode(), BizCode.PARAM_INVALID.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleOther(Exception ex) {
        log.error("未捕获异常", ex);
        return ApiResponse.fail(BizCode.SYSTEM_ERROR.getCode(), BizCode.SYSTEM_ERROR.getMessage());
    }
}
