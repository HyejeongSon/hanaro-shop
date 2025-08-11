package com.hanaro.shop.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // 일반적인 에러
    INVALID_INPUT("E001", "잘못된 입력값입니다", HttpStatus.BAD_REQUEST),
    SERVER_ERROR("E002", "서버 내부 오류가 발생했습니다", HttpStatus.INTERNAL_SERVER_ERROR),
    
    // 인증/인가 관련
    UNAUTHORIZED("A001", "인증이 필요합니다", HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED("A002", "접근 권한이 없습니다", HttpStatus.FORBIDDEN),
    INVALID_TOKEN("A003", "유효하지 않은 토큰입니다", HttpStatus.UNAUTHORIZED),
    EXPIRED_TOKEN("A004", "만료된 토큰입니다", HttpStatus.UNAUTHORIZED),
    
    // 사용자 관련
    MEMBER_NOT_FOUND("M001", "사용자를 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    EMAIL_ALREADY_EXISTS("M002", "이미 존재하는 이메일입니다", HttpStatus.CONFLICT),
    INVALID_PASSWORD("M003", "비밀번호가 일치하지 않습니다", HttpStatus.BAD_REQUEST),
    
    // 상품 관련
    PRODUCT_NOT_FOUND("P001", "상품을 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    PRODUCT_OUT_OF_STOCK("P002", "재고가 부족합니다", HttpStatus.BAD_REQUEST),
    PRODUCT_INACTIVE("P003", "비활성화된 상품입니다", HttpStatus.BAD_REQUEST),
    
    // 장바구니 관련
    CART_NOT_FOUND("C001", "장바구니를 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    CART_ITEM_NOT_FOUND("C002", "장바구니에서 해당 상품을 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    INVALID_QUANTITY("C003", "잘못된 수량입니다", HttpStatus.BAD_REQUEST),
    CART_EMPTY("C004", "장바구니가 비어있습니다", HttpStatus.BAD_REQUEST),
    
    // 주문 관련
    ORDER_NOT_FOUND("O001", "주문을 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    ORDER_ALREADY_CANCELED("O002", "이미 취소된 주문입니다", HttpStatus.BAD_REQUEST),
    ORDER_CANNOT_CANCEL("O003", "취소할 수 없는 주문입니다", HttpStatus.BAD_REQUEST),
    INSUFFICIENT_STOCK("O005", "재고가 부족합니다", HttpStatus.BAD_REQUEST),
    
    // 배송 관련
    DELIVERY_NOT_FOUND("D001", "배송 정보를 찾을 수 없습니다", HttpStatus.NOT_FOUND),
    INVALID_DELIVERY_STATUS("D002", "잘못된 배송 상태입니다", HttpStatus.BAD_REQUEST),
    
    // 파일 업로드 관련
    FILE_UPLOAD_ERROR("F001", "파일 업로드 중 오류가 발생했습니다", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_FILE_TYPE("F002", "지원하지 않는 파일 형식입니다", HttpStatus.BAD_REQUEST),
    FILE_SIZE_EXCEEDED("F003", "파일 크기가 제한을 초과했습니다", HttpStatus.BAD_REQUEST);
    
    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}