package me.leozdgao.beaver.client;

import lombok.Data;

/**
 * 通用响应类
 * @author leozdgao
 */
@Data
public class Response<T> {
    private boolean success;
    private T data;
    private String code;
    private String message;

    public static <T> Response<T> buildSuccess() {
        return buildSuccess(null);
    }

    public static <T> Response<T> buildSuccess(T data) {
        Response<T> res = new Response<>();
        res.setSuccess(true);
        res.setData(data);
        return res;
    }

    public static <T> Response<T> buildFailure(String message) {
        return buildFailure("SYS_ERROR", message);
    }

    public static <T> Response<T> buildFailure(String code, String message) {
        Response<T> res = new Response<>();
        res.setSuccess(false);
        res.setCode(code);
        res.setMessage(message);
        return res;
    }
}
