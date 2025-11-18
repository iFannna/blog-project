package com.sau.exception;

import com.sau.pojo.entity.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     */
    @ExceptionHandler(RuntimeException.class)
    public Result handleRuntimeException(RuntimeException e) {
        log.error("运行时异常: ", e);
        return Result.error("服务异常，请稍后再试");
    }

    /**
     * 处理通用异常
     */
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        log.error("系统异常: ", e);
        return Result.error("系统错误，请联系管理员");
    }

    /**
     * 处理邮箱已存在异常
     */
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public Result handleEmailAlreadyExistsException(EmailAlreadyExistsException e) {
        log.warn("邮箱已被注册: ", e);
        return Result.error(e.getMessage());
    }

    /**
     * 处理数据库重复键异常
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public void handleDuplicateKeyException(DuplicateKeyException e) {
        log.warn("数据库重复键异常: ", e);
    }

    /**
     * 处理未登录或令牌无效异常
     */
    @ExceptionHandler(TokenInvalidException.class)
    public Result handleTokenInvalidException(TokenInvalidException e) {
        log.warn("未登录或令牌无效: ", e);
        return Result.error(e.getMessage());
    }
}