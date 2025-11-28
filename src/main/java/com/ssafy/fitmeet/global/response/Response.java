package com.ssafy.fitmeet.global.response;

import com.ssafy.fitmeet.global.error.ErrorCode;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

// response 클래스
public class Response<T> {

    private int code;
    private String msg;
    private T data;

    public static <T> Response<T> success(T data) {
        return Response.<T>builder()
                .code(ErrorCode.SUCCESS.getCode())
                .msg(ErrorCode.SUCCESS.getMessage())
                .data(data)
                .build();
    }

    public static <T> Response<T> fromErrorCode(ErrorCode errorCode) {
        return Response.<T>builder()
                .code(errorCode.getCode())
                .msg(errorCode.getMessage())
                .data(null)
                .build();
    }

    public static <T> Response<T> fromErrorCode(ErrorCode errorCode, String message) {
        return Response.<T>builder()
                .code(errorCode.getCode())
                .msg(message)
                .data(null)
                .build();
    }
}
