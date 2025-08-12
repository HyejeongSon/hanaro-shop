package com.hanaro.shop.repository;

import com.hanaro.shop.domain.DailyProductStatistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DailyProductStatisticsRepository extends JpaRepository<DailyProductStatistics, Long> {
    
    @Query("SELECT dps FROM DailyProductStatistics dps " +
           "JOIN FETCH dps.product p " +
           "WHERE dps.statisticsDate = :date " +
           "ORDER BY dps.revenue DESC")
    List<DailyProductStatistics> findByStatisticsDateWithProduct(@Param("date") LocalDate date);
    
    @Query("SELECT dps FROM DailyProductStatistics dps " +
           "JOIN FETCH dps.product p " +
           "WHERE dps.statisticsDate BETWEEN :startDate AND :endDate " +
           "ORDER BY dps.statisticsDate DESC, dps.revenue DESC")
    List<DailyProductStatistics> findByStatisticsDateBetweenWithProduct(
            @Param("startDate") LocalDate startDate, 
            @Param("endDate") LocalDate endDate);
}