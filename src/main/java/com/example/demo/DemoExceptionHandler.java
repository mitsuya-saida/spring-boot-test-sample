package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class DemoExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
        return super.handleExceptionInternal(ex, ex.getStackTrace(), null, HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {

        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> String.format("%s %s", e.getField(), e.getDefaultMessage()))
                .collect(Collectors.joining(", "));

        return new ResponseEntity<>(
                ex.getStackTrace().toString(),
                headers,
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(
            IllegalArgumentException ie, WebRequest request) {

        return handleExceptionInternal(
                ie, ie.getStackTrace().toString(), null, HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {

        return super.handleExceptionInternal(ex, body, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        ex.getBindingResult().getAllErrors().forEach(error -> {
            log.warn(error.getCodes()[1] + error.getDefaultMessage());
        });
        return super.handleExceptionInternal(ex, "validation error", headers, status, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleBaseException(Exception ex, WebRequest request) {
        log.error(ex.getMessage());

        return handleExceptionInternal(
                ex, ex.getStackTrace().toString(), null, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }
}
