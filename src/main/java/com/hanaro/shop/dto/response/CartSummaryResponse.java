package com.hanaro.shop.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "장바구니 요약 정보")
public class CartSummaryResponse {

    @Schema(description = "회원 ID", example = "1")
    private Long memberId;

    @Schema(description = "총 상품 수량", example = "5")
    private Integer totalQuantity;

    @Schema(description = "상품 종류 수", example = "3")
    private Integer totalTypes;

    @Schema(description = "총 금액", example = "150000")
    private BigDecimal totalPrice;

    @Schema(description = "장바구니 비어있음 여부", example = "false")
    private Boolean isEmpty;
}