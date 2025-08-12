package com.hanaro.shop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailySalesStatisticsResponse {

    private Long id;
    private LocalDate statisticsDate;
    private BigDecimal totalSales;
    private Long totalOrders;
    private Long totalProducts;
    private Long canceledOrders;
    private BigDecimal canceledAmount;
    private BigDecimal averageOrderAmount;
    private List<DailyProductStatisticsResponse> productStatistics;
}