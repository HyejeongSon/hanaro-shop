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

    // @Scheduled(fixedRate = 60000) 1 -> 1 -> 5 or 5 -> 5 -> 10
    @Override
    @Scheduled(fixedRate = 60000) // 1분마다 실행
    public void updateDeliveryStatusToPreparing() {
        LocalDateTime exactTime = LocalDateTime.now().minusMinutes(5);
        
        int updatedCount = deliveryRepository.updateStatusToPreparing(exactTime);
        
        if (updatedCount > 0) {
            log.info("배송 상태 업데이트 완료: {} 건의 배송을 PREPARING 상태로 변경 (5분 후)", updatedCount);
        }
    }

    @Override
    @Scheduled(fixedRate = 60000) // 1분마다 실행
    public void updateDeliveryStatusToShipping() {
        LocalDateTime exactTime = LocalDateTime.now().minusMinutes(15);
        LocalDateTime shippedAt = LocalDateTime.now();
        String trackingNumber = generateTrackingNumber();
        
        int updatedCount = deliveryRepository.updateStatusToShipping(exactTime, shippedAt, trackingNumber);
        
        if (updatedCount > 0) {
            log.info("배송 상태 업데이트 완료: {} 건의 배송을 SHIPPING 상태로 변경 (15분 후)", updatedCount);
        }
    }

    @Override
    @Scheduled(fixedRate = 300000) // 5분마다 실행 (1시간은 좀 더 여유있게)
    public void updateDeliveryStatusToCompleted() {
        LocalDateTime exactTime = LocalDateTime.now().minusHours(1);
        LocalDateTime deliveredAt = LocalDateTime.now();
        
        int updatedCount = deliveryRepository.updateStatusToCompleted(exactTime, deliveredAt);
        
        if (updatedCount > 0) {
            log.info("배송 상태 업데이트 완료: {} 건의 배송을 COMPLETED 상태로 변경 (1시간 후)", updatedCount);
        }
    }


    private String generateTrackingNumber() {
        return "TRK" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }
}