package com.hanaro.shop.controller.user;

import com.hanaro.shop.dto.request.OrderCreateRequest;
import com.hanaro.shop.dto.response.OrderResponse;
import com.hanaro.shop.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('USER')")
@Tag(name = "Order - 주문", description = "주문 관리 API")
public class OrderController {
    
    private final OrderService orderService;
    
    @PostMapping
    @Operation(summary = "장바구니 선택 주문", description = "장바구니에서 선택한 상품들을 주문합니다")
    public ResponseEntity<OrderResponse> createOrderFromCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody OrderCreateRequest request) {
        
        log.info("장바구니 선택 주문 생성 요청: {}, 선택된 아이템: {}", userDetails.getUsername(), request.getCartItemIds());
        OrderResponse response = orderService.createOrderFromCart(userDetails.getUsername(), request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{orderId}")
    @Operation(summary = "주문 상세 조회", description = "주문 ID로 본인의 주문을 상세 조회합니다")
    public ResponseEntity<OrderResponse> getOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long orderId) {
        
        OrderResponse response = orderService.getOrderByUser(userDetails.getUsername(), orderId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    @Operation(summary = "내 주문 목록 조회", description = "본인의 주문 목록을 페이징하여 조회합니다")
    public ResponseEntity<Page<OrderResponse>> getMyOrders(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Page<OrderResponse> response = orderService.getOrdersByUser(userDetails.getUsername(), page, size);
        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/{orderId}/cancel")
    @Operation(summary = "주문 취소", description = "본인의 주문을 취소합니다")
    public ResponseEntity<OrderResponse> cancelOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long orderId) {
        
        log.info("주문 취소 요청: 사용자={}, 주문ID={}", userDetails.getUsername(), orderId);
        OrderResponse response = orderService.cancelOrder(userDetails.getUsername(), orderId);
        return ResponseEntity.ok(response);
    }
}