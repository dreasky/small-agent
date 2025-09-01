package com.qik.agent.common.exception;

import cn.hutool.json.JSONUtil;
import com.qik.agent.common.BaseResponse;
import com.qik.agent.common.ResultUtils;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

// 全局异常处理器，作用于所有@RestController
@Hidden // 关键：让 Knife4j 忽略此类
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 处理参数校验异常（如@Valid注解触发的校验失败）
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseResponse<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        // 提取具体的校验错误信息（字段名 + 错误提示）
        BindingResult bindingResult = ex.getBindingResult();
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : bindingResult.getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        log.error(JSONUtil.toJsonStr(errors));

        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "参数校验失败");
    }

    // 处理自定义业务异常
    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> handleBusinessException(BusinessException ex) {
        log.error(ex.getMessage());
        return ResultUtils.error(ex.getCode(), ex.getMessage());
    }

    // 处理通用异常（作为兜底）
    @ExceptionHandler(Exception.class)
    public BaseResponse<?> handleGeneralException(Exception ex) {
        // todo 生产环境删除详细堆栈stackTrace，避免泄露信息
        log.error("stackTrace: {}", ex.toString());
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, ex.getMessage());
    }
}
