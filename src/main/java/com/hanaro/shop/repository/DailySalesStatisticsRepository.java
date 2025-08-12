package com.hanaro.shop.repository;

import com.hanaro.shop.domain.DailySalesStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface DailySalesStatisticsRepository extends JpaRepository<DailySalesStatistics, Long> {
    
    Optional<DailySalesStatistics> findByStatisticsDate(LocalDate date);
    
    @Query("SELECT COALESCE(SUM(d.totalSales), 0) FROM DailySalesStatistics d " +
           "WHERE d.statisticsDate BETWEEN :startDate AND :endDate")
    Long getTotalSalesBetweenDates(@Param("startDate") LocalDate startDate, 
                                  @Param("endDate") LocalDate endDate);
    
    @Query("SELECT COALESCE(SUM(d.totalOrders), 0) FROM DailySalesStatistics d " +
           "WHERE d.statisticsDate BETWEEN :startDate AND :endDate")
    Long getTotalOrdersBetweenDates(@Param("startDate") LocalDate startDate, 
                                   @Param("endDate") LocalDate endDate);
}