package com.hanaro.shop.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponse {

    private Long id;
    private Long memberId;
    private List<CartItemResponse> items;
    private BigDecimal totalPrice;
    private Integer totalQuantity;
    private boolean isEmpty;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}