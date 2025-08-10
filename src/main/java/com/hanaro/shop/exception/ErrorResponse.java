package com.hanaro.shop.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private String errorCode;
    private String errorMessage;
    private LocalDateTime timestamp;
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, String> validationErrors;

    public ErrorResponse(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.timestamp = LocalDateTime.now();
    }
    
    public ErrorResponse(ErrorCode errorCode) {
        this.errorCode = errorCode.getCode();
        this.errorMessage = errorCode.getMessage();
        this.timestamp = LocalDateTime.now();
    }
}