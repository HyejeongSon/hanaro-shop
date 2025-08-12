package com.hanaro.shop.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "deliveries")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Delivery extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @NotBlank(message = "배송지 주소는 필수입니다")
    @Column(nullable = false)
    private String address;

    @Column
    private String phone;

    @Column
    private String recipientName;

    @Column
    private String deliveryRequest;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private DeliveryStatus status = DeliveryStatus.PENDING;

    @Column
    private String trackingNumber;

    @Column
    private LocalDateTime shippedAt;

    @Column
    private LocalDateTime deliveredAt;

    public void setOrder(Order order) {
        this.order = order;
    }

    public void updateStatus(DeliveryStatus status) {
        this.status = status;
        
        switch (status) {
            case SHIPPING:
                this.shippedAt = LocalDateTime.now();
                break;
            case COMPLETED:
                this.deliveredAt = LocalDateTime.now();
                break;
            default:
                break;
        }
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public void updateDeliveryInfo(String address, String phone, String recipientName, String deliveryRequest) {
        this.address = address;
        this.phone = phone;
        this.recipientName = recipientName;
        this.deliveryRequest = deliveryRequest;
    }

    public boolean canUpdateStatus(DeliveryStatus newStatus) {
        switch (this.status) {
            case PENDING:
                return newStatus == DeliveryStatus.PREPARING;
            case PREPARING:
                return newStatus == DeliveryStatus.SHIPPING;
            case SHIPPING:
                return newStatus == DeliveryStatus.COMPLETED;
            case COMPLETED:
                return false;
            case CANCELED:
                return false;
            default:
                return false;
        }
    }
    
    public void cancelDelivery() {
        if (this.status != DeliveryStatus.PENDING) {
            throw new IllegalStateException("배송 준비 전 상태에서만 취소할 수 있습니다.");
        }
        this.status = DeliveryStatus.CANCELED;
    }

    public void startShipping(String trackingNumber) {
        if (!canUpdateStatus(DeliveryStatus.SHIPPING)) {
            throw new IllegalStateException("배송 준비 상태에서만 배송 시작이 가능합니다.");
        }
        this.status = DeliveryStatus.SHIPPING;
        this.trackingNumber = trackingNumber;
        this.shippedAt = LocalDateTime.now();
    }

    public void completeDelivery() {
        if (!canUpdateStatus(DeliveryStatus.COMPLETED)) {
            throw new IllegalStateException("배송 중 상태에서만 배송 완료가 가능합니다.");
        }
        this.status = DeliveryStatus.COMPLETED;
        this.deliveredAt = LocalDateTime.now();
    }
}