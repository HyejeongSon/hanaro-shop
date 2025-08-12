package com.hanaro.shop.controller;

import com.hanaro.shop.domain.ProductCategory;
import com.hanaro.shop.dto.response.ProductResponse;
import com.hanaro.shop.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Product - 상품 조회", description = "상품 조회 API (사용자/관리자 공용)")
public class ProductController {

    private final ProductService productService;

    @GetMapping("/{productId}")
    @Operation(summary = "상품 조회", description = "특정 상품의 상세 정보를 조회합니다.")
    public ResponseEntity<ProductResponse> getProduct(
            @Parameter(description = "상품 ID", required = true) @PathVariable Long productId) {

        ProductResponse product = productService.getProduct(productId);
        return ResponseEntity.ok(product);
    }

    @GetMapping
    @Operation(summary = "상품 목록 조회", description = "상품 목록을 페이징하여 조회합니다.")
    public ResponseEntity<Page<ProductResponse>> getProducts(
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<ProductResponse> products = productService.getProducts(pageable);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "카테고리별 상품 조회", description = "특정 카테고리의 상품 목록을 조회합니다.")
    public ResponseEntity<List<ProductResponse>> getProductsByCategory(
            @Parameter(description = "상품 카테고리", required = true) @PathVariable ProductCategory category) {
        
        List<ProductResponse> products = productService.getProductsByCategory(category);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/search")
    @Operation(summary = "상품 검색", description = "키워드로 상품을 검색합니다.")
    public ResponseEntity<List<ProductResponse>> searchProducts(
            @Parameter(description = "검색 키워드", required = true) @RequestParam String keyword) {
        
        List<ProductResponse> products = productService.searchProducts(keyword);
        return ResponseEntity.ok(products);
    }
}