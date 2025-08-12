package com.hanaro.shop.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "daily_sales_statistics")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class DailySalesStatistics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private LocalDate statisticsDate;

    @Column(nullable = false, precision = 10, scale = 0)
    private BigDecimal totalSales;

    @Column(nullable = false)
    private Long totalOrders;

    @Column(nullable = false)
    private Long totalProducts;

    @Column(nullable = false)
    private Long canceledOrders;

    @Column(nullable = false, precision = 10, scale = 0)
    private BigDecimal canceledAmount;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal averageOrderAmount;

    @OneToMany(mappedBy = "dailySalesStatistics", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DailyProductStatistics> productStatistics;

    public static DailySalesStatistics createStatistics(
            LocalDate date,
            BigDecimal totalSales,
            Long totalOrders,
            Long totalProducts,
            Long canceledOrders,
            BigDecimal canceledAmount) {
        
        BigDecimal averageOrderAmount = totalOrders > 0 ? 
            totalSales.divide(BigDecimal.valueOf(totalOrders), 2, BigDecimal.ROUND_HALF_UP) : 
            BigDecimal.ZERO;

        return DailySalesStatistics.builder()
                .statisticsDate(date)
                .totalSales(totalSales)
                .totalOrders(totalOrders)
                .totalProducts(totalProducts)
                .canceledOrders(canceledOrders)
                .canceledAmount(canceledAmount)
                .averageOrderAmount(averageOrderAmount)
                .build();
    }
}