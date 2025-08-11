package com.hanaro.shop.dto.response;

import com.hanaro.shop.domain.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {
    
    private Long id;
    private String orderNumber;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private String deliveryAddress;
    private String deliveryPhone;
    private String deliveryRequest;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 주문자 정보 (관리자용)
    private String memberEmail;
    private String memberName;
    
    // 주문 상품 목록
    private List<OrderItemResponse> orderItems;
    
    // 배송 정보
    private DeliveryResponse delivery;
    
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemResponse {
        private Long id;
        private Long productId;
        private String productName;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal totalPrice;
        
        // 상품 썸네일 이미지 URL
        private String thumbnailUrl;
    }
}