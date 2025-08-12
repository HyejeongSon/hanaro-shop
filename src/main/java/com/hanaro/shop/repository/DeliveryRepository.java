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
    @Query("UPDATE Delivery d SET d.status = com.hanaro.shop.domain.DeliveryStatus.PREPARING " +
           "WHERE d.status = com.hanaro.shop.domain.DeliveryStatus.PENDING " +
           "AND d.createdAt <= :cutoffTime")
    int updateStatusToPreparing(@Param("cutoffTime") LocalDateTime cutoffTime);

    @Modifying
    @Query("UPDATE Delivery d SET d.status = com.hanaro.shop.domain.DeliveryStatus.SHIPPING, " +
           "d.shippedAt = :shippedAt, d.trackingNumber = :trackingNumber " +
           "WHERE d.status = com.hanaro.shop.domain.DeliveryStatus.PREPARING " +
           "AND d.createdAt <= :cutoffTime")
    int updateStatusToShipping(@Param("cutoffTime") LocalDateTime cutoffTime,
                              @Param("shippedAt") LocalDateTime shippedAt,
                              @Param("trackingNumber") String trackingNumber);

    @Modifying
    @Query("UPDATE Delivery d SET d.status = com.hanaro.shop.domain.DeliveryStatus.COMPLETED, " +
           "d.deliveredAt = :deliveredAt " +
           "WHERE d.status = com.hanaro.shop.domain.DeliveryStatus.SHIPPING " +
           "AND d.shippedAt <= :cutoffTime")
    int updateStatusToCompleted(@Param("cutoffTime") LocalDateTime cutoffTime,
                               @Param("deliveredAt") LocalDateTime deliveredAt);


}