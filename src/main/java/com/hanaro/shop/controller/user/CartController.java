package com.hanaro.shop.controller.user;

import com.hanaro.shop.dto.request.CartItemRequest;
import com.hanaro.shop.dto.response.CartResponse;
import com.hanaro.shop.dto.response.CartSummaryResponse;
import com.hanaro.shop.security.CustomUserDetails;
import com.hanaro.shop.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
@Tag(name = "Cart - 장바구니", description = "장바구니 관리 API")
public class CartController {

    private final CartService cartService;

    @GetMapping
    @Operation(summary = "내 장바구니 조회", description = "현재 로그인한 사용자의 장바구니를 조회합니다.")
    public ResponseEntity<CartResponse> getMyCart(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        CartResponse cart = cartService.getCart(userDetails.getMember().getId());
        return ResponseEntity.ok(cart);
    }

    @PostMapping("/items")
    @Operation(summary = "장바구니에 상품 추가", description = "장바구니에 상품을 추가합니다.")
    public ResponseEntity<CartResponse> addItemToCart(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "장바구니 상품 추가 요청", required = true)
            @RequestBody @Valid CartItemRequest request) {
        
        CartResponse cart = cartService.addItemToCart(userDetails.getMember().getId(), request);
        return ResponseEntity.ok(cart);
    }

    @PutMapping("/items/{productId}")
    @Operation(summary = "장바구니 상품 수량 변경", description = "장바구니에 있는 상품의 수량을 변경합니다.")
    public ResponseEntity<CartResponse> updateCartItem(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "상품 ID", required = true) @PathVariable Long productId,
            @Parameter(description = "변경할 수량", required = true) @RequestParam Integer quantity) {
        
        CartResponse cart = cartService.updateCartItem(userDetails.getMember().getId(), productId, quantity);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/items/{productId}")
    @Operation(summary = "장바구니에서 상품 제거", description = "장바구니에서 특정 상품을 제거합니다.")
    public ResponseEntity<CartResponse> removeItemFromCart(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "상품 ID", required = true) @PathVariable Long productId) {
        
        CartResponse cart = cartService.removeItemFromCart(userDetails.getMember().getId(), productId);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping
    @Operation(summary = "장바구니 전체 비우기", description = "장바구니의 모든 상품을 제거합니다.")
    public ResponseEntity<Void> clearCart(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        cartService.clearCart(userDetails.getMember().getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/count")
    @Operation(summary = "장바구니 상품 개수 조회", description = "장바구니에 담긴 상품의 총 개수를 조회합니다.")
    public ResponseEntity<Integer> getCartItemCount(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        int count = cartService.getCartItemCount(userDetails.getMember().getId());
        return ResponseEntity.ok(count);
    }

    @GetMapping("/types")
    @Operation(summary = "장바구니 상품 종류 개수 조회", description = "장바구니에 담긴 상품 종류의 개수를 조회합니다.")
    public ResponseEntity<Integer> getCartItemTypes(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        int types = cartService.getCartItemTypes(userDetails.getMember().getId());
        return ResponseEntity.ok(types);
    }

    @GetMapping("/summary")
    @Operation(summary = "장바구니 요약 정보 조회", description = "장바구니의 요약 정보(총 개수, 종류 수, 총 금액 등)를 조회합니다.")
    public ResponseEntity<CartSummaryResponse> getCartSummary(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        CartSummaryResponse summary = cartService.getCartSummary(userDetails.getMember().getId());
        return ResponseEntity.ok(summary);
    }

    @PatchMapping("/items/{productId}/increase")
    @Operation(summary = "장바구니 상품 수량 증가", description = "장바구니에 있는 상품의 수량을 증가시킵니다.")
    public ResponseEntity<CartResponse> increaseCartItem(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "상품 ID", required = true) @PathVariable Long productId,
            @Parameter(description = "증가할 수량", required = false) @RequestParam(defaultValue = "1") Integer amount) {
        
        CartResponse cart = cartService.increaseCartItem(userDetails.getMember().getId(), productId, amount);
        return ResponseEntity.ok(cart);
    }

    @PatchMapping("/items/{productId}/decrease")
    @Operation(summary = "장바구니 상품 수량 감소", description = "장바구니에 있는 상품의 수량을 감소시킵니다. 수량이 0이 되면 해당 상품을 장바구니에서 제거합니다.")
    public ResponseEntity<CartResponse> decreaseCartItem(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "상품 ID", required = true) @PathVariable Long productId,
            @Parameter(description = "감소할 수량", required = false) @RequestParam(defaultValue = "1") Integer amount) {
        
        CartResponse cart = cartService.decreaseCartItem(userDetails.getMember().getId(), productId, amount);
        return ResponseEntity.ok(cart);
    }
}