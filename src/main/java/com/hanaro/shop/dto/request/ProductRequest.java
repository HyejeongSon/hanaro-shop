package com.hanaro.shop.dto.request;

import com.hanaro.shop.domain.ProductCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "상품 등록/수정 요청")
public class ProductRequest {

    @NotBlank(message = "상품명은 필수입니다")
    @Schema(description = "상품명", example = "갤럭시 S25")
    private String name;

    @NotBlank(message = "상품 설명은 필수입니다")
    @Schema(description = "상품 설명", example = "최신 스마트폰입니다")
    private String description;

    @NotNull(message = "가격은 필수입니다")
    @Min(value = 0, message = "가격은 0 이상이어야 합니다")
    @Schema(description = "가격", example = "1200000")
    private BigDecimal price;

    @NotNull(message = "재고 수량은 필수입니다")
    @Min(value = 0, message = "재고 수량은 0 이상이어야 합니다")
    @Schema(description = "재고 수량", example = "100")
    private Integer stockQuantity;

    @NotNull(message = "카테고리는 필수입니다")
    @Schema(description = "상품 카테고리", example = "ELECTRONICS")
    private ProductCategory category;

    @Schema(description = "상품 이미지 파일들")
    private List<MultipartFile> images;
}