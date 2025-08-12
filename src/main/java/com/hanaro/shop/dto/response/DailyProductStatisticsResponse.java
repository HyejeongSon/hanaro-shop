package com.hanaro.shop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyProductStatisticsResponse {

    private Long id;
    private LocalDate statisticsDate;
    private ProductSummaryResponse product;
    private Long quantitySold;
    private BigDecimal revenue;
    private Long orderCount;
}