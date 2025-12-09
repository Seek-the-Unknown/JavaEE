package com.config;

import com.utils.Result; // 1. 导入我们之前创建的统一响应类
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// @RestControllerAdvice 注解表示这是一个全局的异常处理组件
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * ======== 捕获我们自定义的业务异常 (RuntimeException) ========
     * @ExceptionHandler(RuntimeException.class) 表示这个方法专门处理运行时异常
     * @param e 捕获到的异常对象
     * @return 返回给前端的统一响应体
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 2. 返回 400 状态码，表示这是一个客户端请求错误
    public Result<?> handleRuntimeException(RuntimeException e) {
        log.error("业务异常: {}", e.getMessage()); // 3. 在后端日志中记录错误
        // 4. 将异常中的友好提示信息，包装成Result对象返回给前端
        return Result.error(400, e.getMessage());
    }

    /**
     * ======== 捕获所有其他未处理的异常 (Exception) ========
     * 这是最后的防线，防止任何未预料的错误导致服务器崩溃
     * @param e 捕获到的异常对象
     * @return 返回给前端的统一响应体
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 5. 返回 500 状态码
    public Result<?> handleException(Exception e) {
        log.error("系统未知异常: ", e); // 6. 记录完整的异常堆栈
        // 7. 返回一个通用的、不暴露内部细节的错误信息
        return Result.error(500, "服务器内部发生未知错误，请联系管理员");
    }
}