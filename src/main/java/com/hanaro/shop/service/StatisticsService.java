package com.hanaro.shop.service;

import com.hanaro.shop.domain.DailySalesStatistics;
import com.hanaro.shop.domain.DailyProductStatistics;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface StatisticsService {
    
    // 일별 매출 통계 조회
    Page<DailySalesStatistics> getDailySalesStatistics(int page, int size);
    
    // 특정 날짜 매출 통계 조회
    DailySalesStatistics getDailySalesStatistics(LocalDate date);
    
    // 특정 날짜 상품별 통계 조회
    List<DailyProductStatistics> getProductStatistics(LocalDate date);
    
    // 기간별 상품별 통계 조회
    List<DailyProductStatistics> getProductStatisticsRange(LocalDate startDate, LocalDate endDate);
    
    // 통계 수동 생성 (테스트용)
    void generateStatistics();
}