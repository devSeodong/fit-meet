package com.ssafy.fitmeet.global.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 0번대: 성공
    SUCCESS(0, HttpStatus.OK, "성공"),

    // 1000번대: 인증/인가 관련
    AUTH_INVALID_TOKEN(1001, HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    AUTH_EXPIRED_TOKEN(1002, HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
    AUTH_ACCESS_DENIED(1003, HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    AUTH_BAD_CREDENTIALS(1004, HttpStatus.UNAUTHORIZED, "아이디 혹은 비밀번호가 틀렸습니다."),
    AUTH_INVALID_REFRESH_TOKEN(1005, HttpStatus.UNAUTHORIZED, "리프레시 토큰이 유효하지 않습니다."),
    AUTH_NON_SAVED_REFRESH_TOKEN(1006, HttpStatus.UNAUTHORIZED, "저장된 리프레시 토큰 정보가 없습니다."),

    // 2000번대: 유저 관련
    USER_NOT_FOUND(2001, HttpStatus.BAD_REQUEST, "유저를 찾을 수 없습니다."),
    USER_EMAIL_DUPLICATED(2002, HttpStatus.BAD_REQUEST, "이미 존재하는 이메일입니다."),

    // 3000번대: 비즈니스 도메인 (예: 챌린지, 식단 등) – 필요하면 추가
    // CHALLENGE_NOT_FOUND(3001, HttpStatus.BAD_REQUEST, "챌린지를 찾을 수 없습니다."),

    // 8000번대: DB 관련
    DB_DUPLICATE_KEY(8001, HttpStatus.CONFLICT, "이미 존재하는 데이터입니다."),
    DB_ERROR(8002, HttpStatus.INTERNAL_SERVER_ERROR, "데이터베이스 오류가 발생했습니다."),

    // 9000번대: 서버/기타
    INTERNAL_SERVER_ERROR(9000, HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");

    private final int code;
    private final HttpStatus status;
    private final String message;

    ErrorCode(int code, HttpStatus status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }
}
