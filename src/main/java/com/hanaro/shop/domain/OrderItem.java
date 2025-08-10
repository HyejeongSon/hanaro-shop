package com.hanaro.shop.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class OrderItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @NotNull(message = "상품명은 필수입니다")
    @Column(nullable = false)
    private String productName;

    @NotNull(message = "수량은 필수입니다")
    @Min(value = 1, message = "수량은 1 이상이어야 합니다")
    @Column(nullable = false)
    private Integer quantity;

    @NotNull(message = "단가는 필수입니다")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @NotNull(message = "총 가격은 필수입니다")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    public void setOrder(Order order) {
        this.order = order;
    }

    public void setProduct(Product product) {
        this.product = product;
        this.productName = product.getName();
        this.unitPrice = product.getPrice();
        calculateTotalPrice();
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        calculateTotalPrice();
    }

    private void calculateTotalPrice() {
        if (this.unitPrice != null && this.quantity != null) {
            this.totalPrice = this.unitPrice.multiply(BigDecimal.valueOf(this.quantity));
        }
    }

    public static OrderItem createOrderItem(Product product, Integer quantity) {
        // 재고 확인
        if (product.getStockQuantity() < quantity) {
            throw new IllegalArgumentException("재고가 부족합니다. 요청수량: " + quantity + ", 재고: " + product.getStockQuantity());
        }

        // 재고 차감
        product.decreaseStock(quantity);

        return OrderItem.builder()
                .product(product)
                .productName(product.getName())
                .quantity(quantity)
                .unitPrice(product.getPrice())
                .totalPrice(product.getPrice().multiply(BigDecimal.valueOf(quantity)))
                .build();
    }
}