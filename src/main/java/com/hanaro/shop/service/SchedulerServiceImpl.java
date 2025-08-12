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
        log.info("[DELIVERY_SCHEDULER] Starting PENDING to PREPARING status update");
        int updatedCount = deliveryRepository.updateAllPendingToPreparing();
        
        if (updatedCount > 0) {
            log.info("[DELIVERY_SCHEDULER] Delivery status updated: {} deliveries changed to PREPARING", updatedCount);
        } else {
            log.info("[DELIVERY_SCHEDULER] No deliveries to update to PREPARING status");
        }
    }

    @Override
    @Scheduled(cron = "0 */15 * * * *") // 15분마다 실행
    public void updateDeliveryStatusToShipping() {
        log.info("[DELIVERY_SCHEDULER] Starting PREPARING to SHIPPING status update");
        LocalDateTime shippedAt = LocalDateTime.now();
        String trackingNumber = generateTrackingNumber();
        
        int updatedCount = deliveryRepository.updateAllPreparingToShipping(shippedAt, trackingNumber);
        
        if (updatedCount > 0) {
            log.info("[DELIVERY_SCHEDULER] Delivery status updated: {} deliveries changed to SHIPPING at: {}",
                    updatedCount, shippedAt);
        } else {
            log.info("[DELIVERY_SCHEDULER] No deliveries to update to SHIPPING status");
        }
    }

    @Override
    @Scheduled(cron = "0 0 * * * *") // 1시간마다 실행
    public void updateDeliveryStatusToCompleted() {
        log.info("[DELIVERY_SCHEDULER] Starting SHIPPING to COMPLETED status update");
        LocalDateTime deliveredAt = LocalDateTime.now();
        
        int updatedCount = deliveryRepository.updateAllShippingToCompleted(deliveredAt);
        
        if (updatedCount > 0) {
            log.info("[DELIVERY_SCHEDULER] Delivery status updated: {} deliveries changed to COMPLETED at {}", 
                    updatedCount, deliveredAt);
        } else {
            log.info("[DELIVERY_SCHEDULER] No deliveries to update to COMPLETED status");
        }
    }


    private String generateTrackingNumber() {
        return "TRK" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
}