package com.hanaro.shop.service;

import com.hanaro.shop.dto.response.DeliveryResponse;

public interface DeliveryService {
    
    // 사용자: 자신의 주문 배송 조회
    DeliveryResponse getDeliveryByOrderId(Long orderId, String memberEmail);
}