package com.hanaro.shop.dto.request;

import com.hanaro.shop.domain.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderSearchRequest {
    
    private String orderNumber;
    private String memberEmail;
    private OrderStatus status;
    
    // 페이징 정보
    private int page = 0;
    private int size = 10;
}