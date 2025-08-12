package com.hanaro.shop.controller.admin;

import com.hanaro.shop.dto.request.ProductRequest;
import com.hanaro.shop.dto.response.ProductResponse;
import com.hanaro.shop.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
@Tag(name = "Admin - 상품 관리", description = "관리자 전용 상품 관리 API")
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductController {

    private final ProductService productService;

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "상품 등록", description = "새로운 상품을 등록합니다.")
    public ResponseEntity<ProductResponse> createProduct(
            @Parameter(description = "상품 정보", required = true) @Valid @ModelAttribute ProductRequest request) {
        
        ProductResponse product = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @PutMapping(value = "/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "상품 수정", description = "상품 정보를 수정합니다.")
    public ResponseEntity<ProductResponse> updateProduct(
            @Parameter(description = "상품 ID", required = true) @PathVariable Long productId,
            @Parameter(description = "상품 정보", required = true) 
            @Valid @ModelAttribute ProductRequest request) {
        
        ProductResponse product = productService.updateProduct(productId, request);
        return ResponseEntity.ok(product);
    }

    @PatchMapping("/{productId}/stock")
    @Operation(summary = "재고 수량 수정", description = "상품의 재고 수량을 수정합니다.")
    public ResponseEntity<ProductResponse> updateStockQuantity(
            @Parameter(description = "상품 ID", required = true) @PathVariable Long productId,
            @Parameter(description = "재고 수량", required = true) @RequestParam Integer quantity) {
        
        ProductResponse product = productService.updateStockQuantity(productId, quantity);
        return ResponseEntity.ok(product);
    }

    @PatchMapping("/{productId}/status")
    @Operation(summary = "상품 상태 변경", description = "상품의 활성화/비활성화 상태를 변경합니다.")
    public ResponseEntity<ProductResponse> updateProductStatus(
            @Parameter(description = "상품 ID", required = true) @PathVariable Long productId,
            @Parameter(description = "활성화 여부", required = true) @RequestParam Boolean active) {
        
        ProductResponse product = productService.updateProductStatus(productId, active);
        return ResponseEntity.ok(product);
    }

    @DeleteMapping("/{productId}")
    @Operation(summary = "상품 삭제", description = "상품을 삭제합니다.")
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "상품 ID", required = true) @PathVariable Long productId) {
        
        productService.deleteProduct(productId);
        return ResponseEntity.ok().build();
    }
}