package com.contect.hello.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 환자가 없을 때 발생하는 예외를 처리 (예: NoSuchElementException)
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(NoSuchElementException e) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", e.getMessage());
        errorResponse.put("errorCode", "404");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse); // 404 에러와 메시지 반환
    }

    // 그 외 예상치 못한 모든 에러 처리 (500 에러 대용)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleAll(Exception e) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", "서버 내부 오류가 발생했습니다: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
