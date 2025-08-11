package com.hanaro.shop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderSummaryResponse {
    
    // 전체 주문 현황
    private Long totalOrderCount;
    private BigDecimal totalOrderAmount;
    
    // 오늘 주문 현황
    private Long todayOrderCount;
    private BigDecimal todayOrderAmount;
    
    // 상태별 주문 현황
    private Long orderedOrderCount;
    private Long canceledOrderCount;
}