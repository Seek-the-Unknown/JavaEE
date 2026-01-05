package com.example.venue.model;

import lombok.Data;

// 辅助类
@Data
public class Result {
    private int code;
    private String msg;
    private Object data;

    public static Result success(Object d) {
        Result r = new Result();
        r.code = 200;
        r.data = d;
        return r;
    }

    public static Result error(String m) {
        Result r = new Result();
        r.code = 500;
        r.msg = m;
        return r;
    }
}
