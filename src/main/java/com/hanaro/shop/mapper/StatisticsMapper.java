package com.hanaro.shop.mapper;

import com.hanaro.shop.domain.DailyProductStatistics;
import com.hanaro.shop.domain.DailySalesStatistics;
import com.hanaro.shop.domain.Product;
import com.hanaro.shop.dto.response.DailyProductStatisticsResponse;
import com.hanaro.shop.dto.response.DailySalesStatisticsResponse;
import com.hanaro.shop.dto.response.ProductSummaryResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class StatisticsMapper {

    public DailySalesStatisticsResponse toResponse(DailySalesStatistics entity) {
        if (entity == null) {
            return null;
        }

        List<DailyProductStatisticsResponse> productStatistics = null;
        if (entity.getProductStatistics() != null) {
            productStatistics = entity.getProductStatistics().stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList());
        }

        return DailySalesStatisticsResponse.builder()
                .id(entity.getId())
                .statisticsDate(entity.getStatisticsDate())
                .totalSales(entity.getTotalSales())
                .totalOrders(entity.getTotalOrders())
                .totalProducts(entity.getTotalProducts())
                .canceledOrders(entity.getCanceledOrders())
                .canceledAmount(entity.getCanceledAmount())
                .averageOrderAmount(entity.getAverageOrderAmount())
                .productStatistics(productStatistics)
                .build();
    }

    public DailyProductStatisticsResponse toResponse(DailyProductStatistics entity) {
        if (entity == null) {
            return null;
        }

        return DailyProductStatisticsResponse.builder()
                .id(entity.getId())
                .statisticsDate(entity.getStatisticsDate())
                .product(toProductSummary(entity.getProduct()))
                .quantitySold(entity.getQuantitySold())
                .revenue(entity.getRevenue())
                .orderCount(entity.getOrderCount())
                .build();
    }

    public List<DailySalesStatisticsResponse> toResponseList(List<DailySalesStatistics> entities) {
        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<DailyProductStatisticsResponse> toProductResponseList(List<DailyProductStatistics> entities) {
        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private ProductSummaryResponse toProductSummary(Product product) {
        if (product == null) {
            return null;
        }

        String mainImageUrl = null;
        if (product.getMainImage() != null) {
            mainImageUrl = "/upload/" + product.getMainImage().getFilePath();
        }

        return ProductSummaryResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .category(product.getCategory())
                .mainImageUrl(mainImageUrl)
                .build();
    }
}