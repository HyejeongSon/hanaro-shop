package com.hanaro.shop.dto.response;

import com.hanaro.shop.domain.ProductCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSummaryResponse {

    private Long id;
    private String name;
    private BigDecimal price;
    private ProductCategory category;
    private String mainImageUrl;
}