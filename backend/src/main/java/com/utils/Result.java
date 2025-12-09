package com.utils;

import lombok.Data;
import java.io.Serializable;

@Data
public class Result<T> implements Serializable {

    private Integer code; // 状态码, 200表示成功, 其他表示失败
    private String message; // 提示信息
    private T data; // 数据载荷

    // 静态方法，方便快速创建成功的响应
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("操作成功");
        result.setData(data);
        return result;
    }

    // 重载，可以自定义成功消息
    public static <T> Result<T> success(String message, T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage(message);
        result.setData(data);
        return result;
    }

    // 静态方法，方便快速创建失败的响应
    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
}