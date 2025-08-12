package com.hanaro.shop.service;

import com.hanaro.shop.domain.DailySalesStatistics;
import com.hanaro.shop.domain.DailyProductStatistics;
import com.hanaro.shop.exception.BusinessException;
import com.hanaro.shop.exception.ErrorCode;
import com.hanaro.shop.repository.DailySalesStatisticsRepository;
import com.hanaro.shop.repository.DailyProductStatisticsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class StatisticsServiceImpl implements StatisticsService {

    private final DailySalesStatisticsRepository dailySalesStatisticsRepository;
    private final DailyProductStatisticsRepository dailyProductStatisticsRepository;
    private final BatchService batchService;

    @Override
    public Page<DailySalesStatistics> getDailySalesStatistics(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "statisticsDate"));
            return dailySalesStatisticsRepository.findAll(pageable);
        } catch (Exception e) {
            log.error("일별 매출 통계 조회 실패 - page: {}, size: {}, error: {}", page, size, e.getMessage(), e);
            throw new BusinessException(ErrorCode.SERVER_ERROR);
        }
    }

    @Override
    public DailySalesStatistics getDailySalesStatistics(LocalDate date) {
        log.info("일별 매출 통계 조회 - 날짜: {}", date);
        
        return dailySalesStatisticsRepository.findByStatisticsDate(date)
                .orElseThrow(() -> {
                    log.warn("해당 날짜의 통계를 찾을 수 없음 - 날짜: {}", date);
                    return new BusinessException(ErrorCode.STATISTICS_NOT_FOUND);
                });
    }

    @Override
    public List<DailyProductStatistics> getProductStatistics(LocalDate date) {
        log.info("상품별 통계 조회 - 날짜: {}", date);
        
        try {
            List<DailyProductStatistics> statistics = 
                    dailyProductStatisticsRepository.findByStatisticsDateWithProduct(date);
            
            if (statistics.isEmpty()) {
                log.info("해당 날짜의 상품별 통계 없음 - 날짜: {}", date);
            }
            
            return statistics;
        } catch (Exception e) {
            log.error("상품별 통계 조회 실패 - 날짜: {}, error: {}", date, e.getMessage(), e);
            throw new BusinessException(ErrorCode.SERVER_ERROR);
        }
    }

    @Override
    public List<DailyProductStatistics> getProductStatisticsRange(LocalDate startDate, LocalDate endDate) {
        log.info("기간별 상품별 통계 조회 - 시작일: {}, 종료일: {}", startDate, endDate);
        
        if (startDate.isAfter(endDate)) {
            log.warn("잘못된 날짜 범위 - 시작일: {}, 종료일: {}", startDate, endDate);
            throw new BusinessException(ErrorCode.INVALID_INPUT);
        }
        
        try {
            List<DailyProductStatistics> statistics = 
                    dailyProductStatisticsRepository.findByStatisticsDateBetweenWithProduct(startDate, endDate);
            
            if (statistics.isEmpty()) {
                log.info("해당 기간의 상품별 통계 없음 - 시작일: {}, 종료일: {}", startDate, endDate);
            }
            
            return statistics;
        } catch (Exception e) {
            log.error("기간별 상품별 통계 조회 실패 - 시작일: {}, 종료일: {}, error: {}", 
                    startDate, endDate, e.getMessage(), e);
            throw new BusinessException(ErrorCode.SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public void generateStatistics() {
        log.info("통계 수동 생성 시작");
        
        try {
            batchService.generateDailySalesStatistics();
            log.info("통계 수동 생성 완료");
        } catch (Exception e) {
            log.error("통계 수동 생성 실패 - error: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.STATISTICS_GENERATION_FAILED);
        }
    }
}