package com.hanaro.shop.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        log.error("Business exception occurred: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse(ex.getErrorCode());
        return new ResponseEntity<>(errorResponse, ex.getErrorCode().getHttpStatus());
    }

    @ExceptionHandler(CustomJwtException.class)
    public ResponseEntity<ErrorResponse> handleCustomJwtException(CustomJwtException ex) {
        log.error("JWT exception occurred: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.INVALID_TOKEN.getCode(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, ErrorCode.INVALID_TOKEN.getHttpStatus());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        log.error("Access denied: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.ACCESS_DENIED);
        return new ResponseEntity<>(errorResponse, ErrorCode.ACCESS_DENIED.getHttpStatus());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("Illegal argument: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.INVALID_INPUT.getCode(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, ErrorCode.INVALID_INPUT.getHttpStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.SERVER_ERROR);
        return new ResponseEntity<>(errorResponse, ErrorCode.SERVER_ERROR.getHttpStatus());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        
        log.error("Validation failed: {}", ex.getMessage());
        
        Map<String, String> validationErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            validationErrors.put(fieldName, errorMessage);
        });

        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.INVALID_INPUT);
        errorResponse.setValidationErrors(validationErrors);
        
        return new ResponseEntity<>(errorResponse, ErrorCode.INVALID_INPUT.getHttpStatus());
    }

    @Override
    protected ResponseEntity<Object> handleMaxUploadSizeExceededException(
            MaxUploadSizeExceededException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        
        log.error("File size exceeded: {}", ex.getMessage(), ex);
        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.FILE_SIZE_EXCEEDED);
        
        return new ResponseEntity<>(errorResponse, ErrorCode.FILE_SIZE_EXCEEDED.getHttpStatus());
    }
}