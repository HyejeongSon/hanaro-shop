package com.hanaro.shop.domain;

public enum OrderStatus {
    ORDERED,          // 주문 완료 (결제까지 끝난 상태)
    CANCELED,         // 주문 취소
    REFUNDED          // 환불 완료
}
