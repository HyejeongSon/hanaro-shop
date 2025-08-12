package com.hanaro.shop.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "daily_product_statistics",
       uniqueConstraints = @UniqueConstraint(columnNames = {"statistics_date", "product_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class DailyProductStatistics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate statisticsDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daily_sales_statistics_id", nullable = false)
    private DailySalesStatistics dailySalesStatistics;

    @Column(nullable = false)
    private Long quantitySold;

    @Column(nullable = false, precision = 10, scale = 0)
    private BigDecimal revenue;

    @Column(nullable = false)
    private Long orderCount;

    public static DailyProductStatistics createStatistics(
            LocalDate date,
            Product product,
            DailySalesStatistics dailySalesStatistics,
            Long quantitySold,
            BigDecimal revenue,
            Long orderCount) {
        
        return DailyProductStatistics.builder()
                .statisticsDate(date)
                .product(product)
                .dailySalesStatistics(dailySalesStatistics)
                .quantitySold(quantitySold)
                .revenue(revenue)
                .orderCount(orderCount)
                .build();
    }
}