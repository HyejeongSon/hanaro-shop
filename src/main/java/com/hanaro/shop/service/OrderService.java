package com.hanaro.shop.service;

import com.hanaro.shop.dto.request.OrderCreateRequest;
import com.hanaro.shop.dto.request.OrderSearchRequest;
import com.hanaro.shop.dto.response.OrderResponse;
import com.hanaro.shop.dto.response.OrderSummaryResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface OrderService {
    
    // 주문 생성 (장바구니 기반)
    OrderResponse createOrderFromCart(String memberEmail, OrderCreateRequest request);

    // 주문 조회 (사용자)
    OrderResponse getOrderByUser(String memberEmail, Long orderId);
    Page<OrderResponse> getOrdersByUser(String memberEmail, int page, int size);
    
    // 주문 조회 (관리자)
    OrderResponse getOrderById(Long orderId);
    List<OrderResponse> getAllOrders();
    Page<OrderResponse> searchOrders(OrderSearchRequest request);
    
    // 주문 취소
    OrderResponse cancelOrder(String memberEmail, Long orderId);
    OrderResponse cancelOrderByAdmin(Long orderId);
    
    // 주문 통계
    OrderSummaryResponse getOrderSummary();
    
    // 배송 상태 업데이트 (스케줄러용)
    void updateDeliveryStatusScheduled();
}