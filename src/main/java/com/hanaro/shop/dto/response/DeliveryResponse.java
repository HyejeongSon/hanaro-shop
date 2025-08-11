package com.hanaro.shop.dto.response;

import com.hanaro.shop.domain.DeliveryStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryResponse {
    
    private Long id;
    private String address;
    private String phone;
    private String recipientName;
    private String deliveryRequest;
    private DeliveryStatus status;
    private String trackingNumber;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}