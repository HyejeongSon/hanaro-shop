package com.hanaro.shop.controller.admin;

import com.hanaro.shop.domain.DailySalesStatistics;
import com.hanaro.shop.dto.response.DailySalesStatisticsResponse;
import com.hanaro.shop.dto.response.DailyProductStatisticsResponse;
import com.hanaro.shop.service.StatisticsService;
import com.hanaro.shop.mapper.StatisticsMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/admin/statistics")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Order Statistics - 주문 통계 관리", description = "관리자용 통계 관리 API")
@PreAuthorize("hasRole('ADMIN')")
public class AdminStatisticsController {

    private final StatisticsService statisticsService;
    private final StatisticsMapper statisticsMapper;

    @Operation(summary = "일별 매출 통계 조회", description = "페이징 지원")
    @GetMapping("/daily")
    public ResponseEntity<Page<DailySalesStatisticsResponse>> getDailyStatistics(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<DailySalesStatisticsResponse> statistics = statisticsService.getDailySalesStatistics(page, size)
                .map(statisticsMapper::toResponse);
        return ResponseEntity.ok(statistics);
    }

    @Operation(summary = "특정 날짜 매출 통계 조회")
    @GetMapping("/daily/{date}")
    public ResponseEntity<DailySalesStatisticsResponse> getDailyStatistics(@PathVariable LocalDate date) {
        DailySalesStatistics statistics = statisticsService.getDailySalesStatistics(date);
        return ResponseEntity.ok(statisticsMapper.toResponse(statistics));
    }

    @Operation(summary = "특정 날짜 상품별 통계 조회")
    @GetMapping("/products/{date}")
    public ResponseEntity<List<DailyProductStatisticsResponse>> getProductStatistics(@PathVariable LocalDate date) {
        List<DailyProductStatisticsResponse> statistics = statisticsMapper.toProductResponseList(
                statisticsService.getProductStatistics(date));
        return ResponseEntity.ok(statistics);
    }

    @Operation(summary = "기간별 상품별 통계 조회")
    @GetMapping("/products")
    public ResponseEntity<List<DailyProductStatisticsResponse>> getProductStatisticsRange(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        List<DailyProductStatisticsResponse> statistics = statisticsMapper.toProductResponseList(
                statisticsService.getProductStatisticsRange(startDate, endDate));
        return ResponseEntity.ok(statistics);
    }

//    @Operation(summary = "매출 통계 수동 생성", description = "테스트용 - 어제 날짜 기준으로 통계 생성")
//    @PostMapping("/generate")
//    public ResponseEntity<String> generateStatistics() {
//        statisticsService.generateStatistics();
//        return ResponseEntity.ok("통계 생성이 완료되었습니다.");
//    }
}