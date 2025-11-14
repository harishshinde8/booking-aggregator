package com.booking.aggregator.booking.exception.handler;

import com.booking.aggregator.booking.dto.error.ErrorResponse;
import com.booking.aggregator.booking.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // --- NotFoundException handler ---
    @ExceptionHandler(NotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleNotFoundException(NotFoundException ex, ServerWebExchange exchange) {
        log.warn("NotFoundException: {}", ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, ex, exchange);
    }

    // --- Generic RuntimeException handler ---
    @ExceptionHandler(RuntimeException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleRuntimeException(RuntimeException ex, ServerWebExchange exchange) {
        log.error("RuntimeException: {}", ex.getMessage(), ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex, exchange);
    }

    // --- Helper to construct reactive error response ---
    private Mono<ResponseEntity<ErrorResponse>> buildResponse(HttpStatus status, Exception ex, ServerWebExchange exchange) {
        ErrorResponse error = ErrorResponse.builder()
                .timestamp(Instant.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(ex.getMessage())
                .path(exchange.getRequest().getPath().value())
                .build();

        return Mono.just(ResponseEntity.status(status).body(error));
    }


    @ExceptionHandler(MethodNotAllowedException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleMethodNotAllowed(MethodNotAllowedException ex, ServerWebExchange exchange) {
        log.warn("MethodNotAllowedException: {}", ex.getMessage());
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.METHOD_NOT_ALLOWED.value())
                .error(HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase())
                .message(ex.getMessage())
                .path(exchange.getRequest().getPath().value())
                .timestamp(Instant.now())
                .build();
        return Mono.just(ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(error));
    }

    @ExceptionHandler(ServerWebInputException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleInvalidInput(ServerWebInputException ex, ServerWebExchange exchange) {
        log.warn("Invalid input: {}", ex.getMessage());
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(ex.getMessage())
                .path(exchange.getRequest().getPath().value())
                .timestamp(Instant.now())
                .build();
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error));
    }

    // Catch-all
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGenericException(Exception ex, ServerWebExchange exchange) {
        log.error("Unexpected error: {}", ex.getMessage());
        ErrorResponse error = ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message(ex.getMessage())
                .path(exchange.getRequest().getPath().value())
                .timestamp(Instant.now())
                .build();
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error));
    }
}
