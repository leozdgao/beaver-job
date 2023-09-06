package me.leozdgao.beaver.adaptor.web;

import me.leozdgao.beaver.client.Response;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author leozdgao
 */
@RestControllerAdvice
public class GeneralControllerAdvice {
    @ExceptionHandler(IllegalArgumentException.class)
    public Response<Object> handleIllegalArgumentException(IllegalArgumentException e) {
        return Response.buildFailure("INVALID_PARAMS", e.getMessage());
    }

    @ExceptionHandler(Throwable.class)
    public Response<Object> handleException(Throwable e) {
        return Response.buildFailure(e.toString());
    }
}
