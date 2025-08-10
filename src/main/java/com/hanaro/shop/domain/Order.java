package com.hanaro.shop.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(unique = true, nullable = false)
    private String orderNumber;

    @NotNull(message = "총 금액은 필수입니다")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private OrderStatus status = OrderStatus.ORDERED;

    @Column(nullable = false)
    private String deliveryAddress;

    @Column
    private String deliveryPhone;

    @Column
    private String deliveryRequest;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Delivery delivery;

    public void setMember(Member member) {
        this.member = member;
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItem.setOrder(this);
        this.orderItems.add(orderItem);
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    public void updateStatus(OrderStatus status) {
        this.status = status;
    }

    public void cancelOrder() {
        if (this.status != OrderStatus.ORDERED) {
            throw new IllegalStateException("주문 완료 상태에서만 취소할 수 있습니다.");
        }
        this.status = OrderStatus.CANCELED;
        
        // 재고 복구
        orderItems.forEach(orderItem -> 
            orderItem.getProduct().increaseStock(orderItem.getQuantity())
        );
    }

    public void refundOrder() {
        if (this.status == OrderStatus.REFUNDED) {
            throw new IllegalStateException("이미 환불된 주문입니다.");
        }
        this.status = OrderStatus.REFUNDED;
    }

    public BigDecimal calculateTotalAmount() {
        return orderItems.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public boolean canCancel() {
        return this.status == OrderStatus.ORDERED;
    }

    public boolean canRefund() {
        return this.status != OrderStatus.REFUNDED;
    }
}