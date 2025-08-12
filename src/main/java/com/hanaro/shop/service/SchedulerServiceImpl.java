package com.hanaro.shop.service;

import com.hanaro.shop.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SchedulerServiceImpl implements SchedulerService {

    private final DeliveryRepository deliveryRepository;

    @Override
    @Scheduled(cron = "0 */5 * * * *") // 5분마다 실행
    public void updateDeliveryStatusToPreparing() {
        int updatedCount = deliveryRepository.updateAllPendingToPreparing();
        
        if (updatedCount > 0) {
            log.info("배송 상태 업데이트 완료: {} 건의 배송을 PREPARING 상태로 변경", updatedCount);
        }
    }

    @Override
    @Scheduled(cron = "0 */15 * * * *") // 15분마다 실행
    public void updateDeliveryStatusToShipping() {
        LocalDateTime shippedAt = LocalDateTime.now();
        String trackingNumber = generateTrackingNumber();
        
        int updatedCount = deliveryRepository.updateAllPreparingToShipping(shippedAt, trackingNumber);
        
        if (updatedCount > 0) {
            log.info("배송 상태 업데이트 완료: {} 건의 배송을 SHIPPING 상태로 변경", updatedCount);
        }
    }

    @Override
    @Scheduled(cron = "0 0 * * * *") // 1시간마다 실행
    public void updateDeliveryStatusToCompleted() {
        LocalDateTime deliveredAt = LocalDateTime.now();
        
        int updatedCount = deliveryRepository.updateAllShippingToCompleted(deliveredAt);
        
        if (updatedCount > 0) {
            log.info("배송 상태 업데이트 완료: {} 건의 배송을 COMPLETED 상태로 변경", updatedCount);
        }
    }


    private String generateTrackingNumber() {
        return "TRK" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
}