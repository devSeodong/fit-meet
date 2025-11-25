package com.ssafy.fitmeet.global.exception;

import com.ssafy.fitmeet.global.error.CustomException;
import com.ssafy.fitmeet.global.error.ErrorCode;
import com.ssafy.fitmeet.global.response.Response;
import com.ssafy.fitmeet.global.response.ResponseUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

@RestControllerAdvice
@Log4j2
public class GlobalExceptionHandler {

    /**
     * CustomException (비즈니스/도메인 예외)
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Response<?>> handleCustomException(CustomException e) {
        ErrorCode code = e.getErrorCode();
        log.warn("[CustomException] {} - {}", code.name(), e.getMessage());
        return ResponseEntity
                .status(code.getStatus())
                .body(ResponseUtil.error(code, e.getMessage()));
    }

    /**
     * 로그인 실패
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Response<?>> handleBadCredentials(BadCredentialsException e) {
        ErrorCode code = ErrorCode.AUTH_BAD_CREDENTIALS;
        log.warn("[BadCredentials] {}", e.getMessage());
        return ResponseEntity
                .status(code.getStatus())
                .body(ResponseUtil.error(code));
    }

    /**
     * 권한 없음 (Spring Security에서 AccessDeniedException 던질 때)
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Response<?>> handleAccessDenied(AccessDeniedException e) {
        ErrorCode code = ErrorCode.AUTH_ACCESS_DENIED;
        log.warn("[AccessDenied] {}", e.getMessage());
        return ResponseEntity
                .status(code.getStatus())
                .body(ResponseUtil.error(code));
    }

    /**
     * @Valid 검증 실패
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response<?>> handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldError().getDefaultMessage();
        // 필요하면 ErrorCode 따로 빼도 됨
        ErrorCode code = ErrorCode.INTERNAL_SERVER_ERROR; // or VALIDATION_ERROR 새로 추가
        log.warn("[Validation Error] {}", message);
        return ResponseEntity
                .badRequest()
                .body(ResponseUtil.error(code, message));
    }

    /**
     * DB Duplicate Key
     */
    @ExceptionHandler({DuplicateKeyException.class, DataIntegrityViolationException.class})
    public ResponseEntity<Response<?>> handleDuplicateKey(Exception e) {
        ErrorCode code = ErrorCode.DB_DUPLICATE_KEY;
        log.warn("[DuplicateKey] {}", e.getMessage());
        return ResponseEntity
                .status(code.getStatus())
                .body(ResponseUtil.error(code));
    }

    /**
     * 그 외 모든 예외
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response<?>> handleException(Exception e) {
        ErrorCode code = ErrorCode.INTERNAL_SERVER_ERROR;
        log.error("[Exception] {}", e.getMessage(), e);
        return ResponseEntity
                .status(code.getStatus())
                .body(ResponseUtil.error(code));
    }
}

