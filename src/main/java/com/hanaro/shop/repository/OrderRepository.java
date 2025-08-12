package com.hanaro.shop.repository;

import com.hanaro.shop.domain.Member;
import com.hanaro.shop.domain.Order;
import com.hanaro.shop.domain.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    // 회원별 주문 목록 조회 (페이징)
    Page<Order> findByMemberOrderByCreatedAtDesc(Member member, Pageable pageable);
    
    // 회원별 주문 목록 조회 (List)
    List<Order> findByMemberOrderByCreatedAtDesc(Member member);
    
    // 주문 번호로 조회
    Optional<Order> findByOrderNumber(String orderNumber);
    
    // 회원과 주문번호로 조회 (본인 주문 확인용)
    Optional<Order> findByMemberAndOrderNumber(Member member, String orderNumber);
    
    // 주문 검색 (관리자용) - 주문번호, 회원 이메일, 상태로 검색
    @Query("SELECT o FROM Order o JOIN o.member m " +
           "WHERE (:orderNumber IS NULL OR o.orderNumber LIKE %:orderNumber%) " +
           "AND (:memberEmail IS NULL OR m.email LIKE %:memberEmail%) " +
           "AND (:status IS NULL OR o.status = :status) " +
           "ORDER BY o.createdAt DESC")
    Page<Order> findOrdersWithSearch(@Param("orderNumber") String orderNumber,
                                     @Param("memberEmail") String memberEmail,
                                     @Param("status") OrderStatus status,
                                     Pageable pageable);
    
    // 배송 상태 변경을 위한 주문 조회 (스케줄러용)
    @Query("SELECT o FROM Order o JOIN FETCH o.delivery d " +
           "WHERE o.status = :orderStatus AND d.status = :deliveryStatus")
    List<Order> findOrdersForDeliveryStatusUpdate(@Param("orderStatus") OrderStatus orderStatus,
                                                   @Param("deliveryStatus") com.hanaro.shop.domain.DeliveryStatus deliveryStatus);
    
    // 일별 매출 통계 조회 (배치용)
    @Query(value = "SELECT " +
           "COALESCE(SUM(CASE WHEN o.status = 'ORDERED' THEN o.total_amount ELSE 0 END), 0) as totalSales, " +
           "COUNT(CASE WHEN o.status = 'ORDERED' THEN 1 END) as totalOrders, " +
           "COALESCE(SUM(CASE WHEN o.status = 'ORDERED' THEN oi.quantity ELSE 0 END), 0) as totalProducts, " +
           "COUNT(CASE WHEN o.status = 'CANCELED' THEN 1 END) as canceledOrders, " +
           "COALESCE(SUM(CASE WHEN o.status = 'CANCELED' THEN o.total_amount ELSE 0 END), 0) as canceledAmount " +
           "FROM orders o " +
           "LEFT JOIN order_items oi ON o.id = oi.order_id " +
           "WHERE DATE(o.created_at) = :targetDate", 
           nativeQuery = true)
    Map<String, Object> getDailySalesStatistics(@Param("targetDate") LocalDate targetDate);
    
    // 상품별 일별 통계 집계 조회 (배치용)
    @Query(value = "SELECT " +
           "oi.product_id as productId, " +
           "SUM(CASE WHEN o.status = 'ORDERED' THEN oi.quantity ELSE 0 END) as quantitySold, " +
           "SUM(CASE WHEN o.status = 'ORDERED' THEN oi.total_price ELSE 0 END) as revenue, " +
           "COUNT(DISTINCT CASE WHEN o.status = 'ORDERED' THEN o.id END) as orderCount " +
           "FROM orders o " +
           "JOIN order_items oi ON o.id = oi.order_id " +
           "WHERE DATE(o.created_at) = :targetDate " +
           "GROUP BY oi.product_id " +
           "HAVING quantitySold > 0", 
           nativeQuery = true)
    List<Map<String, Object>> getDailyProductStatistics(@Param("targetDate") LocalDate targetDate);
}