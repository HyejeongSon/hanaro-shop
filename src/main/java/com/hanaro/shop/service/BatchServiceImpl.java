package com.hanaro.shop.service;

import com.hanaro.shop.domain.DailySalesStatistics;
import com.hanaro.shop.domain.DailyProductStatistics;
import com.hanaro.shop.domain.Product;
import com.hanaro.shop.exception.BusinessException;
import com.hanaro.shop.exception.ErrorCode;
import com.hanaro.shop.repository.DailySalesStatisticsRepository;
import com.hanaro.shop.repository.DailyProductStatisticsRepository;
import com.hanaro.shop.repository.OrderRepository;
import com.hanaro.shop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BatchServiceImpl implements BatchService {

    private final DailySalesStatisticsRepository dailySalesStatisticsRepository;
    private final DailyProductStatisticsRepository dailyProductStatisticsRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @Override
    @Scheduled(cron = "10 0 0 * * *") // 매일 자정에 실행
    public void generateDailySalesStatistics() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        
        log.info("[BATCH_STATISTICS] Starting daily sales statistics generation for date: {}", yesterday);
        
        if (dailySalesStatisticsRepository.findByStatisticsDate(yesterday).isPresent()) {
            log.info("[BATCH_STATISTICS] Statistics already exists for date: {}. Skipping generation.", yesterday);
            return;
        }
        
        try {
            Map<String, Object> statistics = orderRepository.getDailySalesStatistics(yesterday);
            
            // Native Query 결과를 적절한 타입으로 변환
            BigDecimal totalSales = new BigDecimal(statistics.get("totalSales").toString());
            Long totalOrders = Long.valueOf(statistics.get("totalOrders").toString());
            Long totalProducts = Long.valueOf(statistics.get("totalProducts").toString());
            Long canceledOrders = Long.valueOf(statistics.get("canceledOrders").toString());
            BigDecimal canceledAmount = new BigDecimal(statistics.get("canceledAmount").toString());
            
            // null 값 처리
            totalSales = totalSales != null ? totalSales : BigDecimal.ZERO;
            totalOrders = totalOrders != null ? totalOrders : 0L;
            totalProducts = totalProducts != null ? totalProducts : 0L;
            canceledOrders = canceledOrders != null ? canceledOrders : 0L;
            canceledAmount = canceledAmount != null ? canceledAmount : BigDecimal.ZERO;
            
            DailySalesStatistics dailyStats = DailySalesStatistics.createStatistics(
                    yesterday,
                    totalSales,
                    totalOrders,
                    totalProducts,
                    canceledOrders,
                    canceledAmount
            );
            
            DailySalesStatistics savedDailyStats = dailySalesStatisticsRepository.save(dailyStats);
            
            // 상품별 통계 생성
            generateDailyProductStatistics(yesterday, savedDailyStats);
            
            log.info("[BATCH_STATISTICS] Daily sales statistics generated successfully - date: {}, totalSales: {}, totalOrders: {}, totalProducts: {}, canceledOrders: {}", 
                    yesterday, totalSales, totalOrders, totalProducts, canceledOrders);
                    
        } catch (Exception e) {
            log.error("[BATCH_STATISTICS] Error occurred during daily sales statistics generation - date: {}, error: {}", yesterday, e.getMessage(), e);
            throw new BusinessException(ErrorCode.SERVER_ERROR);
        }
    }
    
    private void generateDailyProductStatistics(LocalDate date, DailySalesStatistics dailyStats) {
        try {
            List<Map<String, Object>> productStatistics = orderRepository.getDailyProductStatistics(date);
            List<DailyProductStatistics> productStatsList = new ArrayList<>();
            
            for (Map<String, Object> productStat : productStatistics) {
                Long productId = Long.valueOf(productStat.get("productId").toString());
                Long quantitySold = Long.valueOf(productStat.get("quantitySold").toString());
                BigDecimal revenue = new BigDecimal(productStat.get("revenue").toString());
                Long orderCount = Long.valueOf(productStat.get("orderCount").toString());
                
                Optional<Product> productOpt = productRepository.findById(productId);
                if (productOpt.isPresent()) {
                    DailyProductStatistics productStats = DailyProductStatistics.createStatistics(
                            date,
                            productOpt.get(),
                            dailyStats,
                            quantitySold,
                            revenue,
                            orderCount
                    );
                    productStatsList.add(productStats);
                } else {
                    log.warn("[BATCH_STATISTICS] Product ID {} not found, excluding from statistics", productId);
                }
            }
            
            if (!productStatsList.isEmpty()) {
                dailyProductStatisticsRepository.saveAll(productStatsList);
                log.info("[BATCH_STATISTICS] Daily product statistics generated: {} products for date: {}", productStatsList.size(), date);
            } else {
                log.info("[BATCH_STATISTICS] No product statistics to generate for date: {}", date);
            }
            
        } catch (Exception e) {
            log.error("[BATCH_STATISTICS] Error occurred during daily product statistics generation - date: {}, error: {}", date, e.getMessage(), e);
            throw new BusinessException(ErrorCode.SERVER_ERROR);
        }
    }
}