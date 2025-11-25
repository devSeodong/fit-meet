package com.ssafy.fitmeet.global.response;

import com.ssafy.fitmeet.global.error.ErrorCode;

public class ResponseUtil {

    public static <T> Response<T> ok(T data) {
        return Response.success(data);
    }

    public static <T> Response<T> ok(String msg, T data) {
        return Response.<T>builder()
                .returnId(ErrorCode.SUCCESS.getCode())
                .returnMsg(msg)
                .data(data)
                .build();
    }

    public static Response<?> error(ErrorCode errorCode) {
        return Response.fromErrorCode(errorCode);
    }

    public static Response<?> error(ErrorCode errorCode, String message) {
        return Response.fromErrorCode(errorCode, message);
    }
}
