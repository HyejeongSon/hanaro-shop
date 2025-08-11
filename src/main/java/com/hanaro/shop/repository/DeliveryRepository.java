package com.hanaro.shop.repository;

import com.hanaro.shop.domain.Delivery;
import com.hanaro.shop.domain.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    
    // 상태별 배송 조회
    List<Delivery> findByStatus(DeliveryStatus status);
    
    // 스케줄러용: 배송 상태 일괄 변경
    @Modifying
    @Query("UPDATE Delivery d SET d.status = :newStatus, d.shippedAt = :shippedAt " +
           "WHERE d.status = :currentStatus")
    int updateDeliveryStatusToShipping(@Param("currentStatus") DeliveryStatus currentStatus,
                                       @Param("newStatus") DeliveryStatus newStatus,
                                       @Param("shippedAt") LocalDateTime shippedAt);
    
    @Modifying
    @Query("UPDATE Delivery d SET d.status = :newStatus, d.deliveredAt = :deliveredAt " +
           "WHERE d.status = :currentStatus")
    int updateDeliveryStatusToCompleted(@Param("currentStatus") DeliveryStatus currentStatus,
                                        @Param("newStatus") DeliveryStatus newStatus,
                                        @Param("deliveredAt") LocalDateTime deliveredAt);
    
    // 특정 시간 전에 생성된 배송 준비 상태 조회 (스케줄러용)
    @Query("SELECT d FROM Delivery d WHERE d.status = :status " +
           "AND d.createdAt <= :beforeTime")
    List<Delivery> findByStatusAndCreatedAtBefore(@Param("status") DeliveryStatus status,
                                                   @Param("beforeTime") LocalDateTime beforeTime);
    
    // 특정 시간 전에 배송 시작된 배송 중 상태 조회 (스케줄러용)
    @Query("SELECT d FROM Delivery d WHERE d.status = :status " +
           "AND d.shippedAt <= :beforeTime")
    List<Delivery> findByStatusAndShippedAtBefore(@Param("status") DeliveryStatus status,
                                                   @Param("beforeTime") LocalDateTime beforeTime);
}