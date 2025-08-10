package com.hanaro.shop.dto.response;

import com.hanaro.shop.domain.ProductCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "상품 응답")
public class ProductResponse {

    @Schema(description = "상품 ID", example = "1")
    private Long id;

    @Schema(description = "상품명", example = "갤럭시 S24")
    private String name;

    @Schema(description = "상품 설명", example = "최신 스마트폰입니다")
    private String description;

    @Schema(description = "가격", example = "1200000")
    private BigDecimal price;

    @Schema(description = "재고 수량", example = "100")
    private Integer stockQuantity;

    @Schema(description = "상품 카테고리", example = "ELECTRONICS")
    private ProductCategory category;

    @Schema(description = "활성 상태", example = "true")
    private Boolean isActive;

    @Schema(description = "상품 이미지 목록")
    private List<ProductImageResponse> images;

    @Schema(description = "생성일시")
    private LocalDateTime createdAt;

    @Schema(description = "수정일시")
    private LocalDateTime updatedAt;
}