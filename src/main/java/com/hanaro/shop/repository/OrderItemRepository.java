package com.hanaro.shop.repository;

import com.hanaro.shop.domain.OrderItem;
import com.hanaro.shop.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
    // 특정 상품의 주문 아이템 조회
    List<OrderItem> findByProduct(Product product);
    
    // 매출 통계용: 상품별 판매 통계 (배치용)
    @Query("SELECT oi.product.id as productId, " +
           "oi.productName as productName, " +
           "SUM(oi.quantity) as totalQuantity, " +
           "SUM(oi.totalPrice) as totalSales, " +
           "COUNT(DISTINCT oi.order.id) as orderCount " +
           "FROM OrderItem oi " +
           "JOIN oi.order o " +
           "WHERE o.status != 'CANCELED' " +
           "AND DATE(o.createdAt) = :targetDate " +
           "GROUP BY oi.product.id, oi.productName " +
           "ORDER BY totalSales DESC")
    List<Object[]> getProductSalesStats(@Param("targetDate") LocalDateTime targetDate);
    
    // 베스트 상품 조회 (일정 기간 내 판매량 기준)
    @Query("SELECT oi.product.id as productId, " +
           "oi.productName as productName, " +
           "SUM(oi.quantity) as totalQuantity " +
           "FROM OrderItem oi " +
           "JOIN oi.order o " +
           "WHERE o.status != 'CANCELED' " +
           "AND o.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY oi.product.id, oi.productName " +
           "ORDER BY totalQuantity DESC")
    List<Object[]> getBestSellingProducts(@Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate);
}