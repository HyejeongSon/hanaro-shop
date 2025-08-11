package com.hanaro.shop.controller.admin;

import com.hanaro.shop.domain.OrderStatus;
import com.hanaro.shop.dto.request.OrderSearchRequest;
import com.hanaro.shop.dto.response.OrderResponse;
import com.hanaro.shop.dto.response.OrderSummaryResponse;
import com.hanaro.shop.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin - 주문 관리", description = "관리자 주문 관리 API")
public class AdminOrderController {
    
    private final OrderService orderService;
    
    @GetMapping
    @Operation(summary = "전체 주문 목록 조회", description = "모든 주문을 조회합니다")
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        List<OrderResponse> response = orderService.getAllOrders();
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{orderId}")
    @Operation(summary = "주문 상세 조회", description = "주문 ID로 주문을 상세 조회합니다")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long orderId) {
        OrderResponse response = orderService.getOrderById(orderId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/search")
    @Operation(summary = "주문 검색", description = "주문번호, 회원 이메일, 주문 상태로 주문을 검색합니다")
    public ResponseEntity<Page<OrderResponse>> searchOrders(
            @RequestParam(required = false) String orderNumber,
            @RequestParam(required = false) String memberEmail,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        OrderSearchRequest request = new OrderSearchRequest(
                orderNumber, memberEmail, status, page, size);
        
        Page<OrderResponse> response = orderService.searchOrders(request);
        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/{orderId}/cancel")
    @Operation(summary = "주문 취소", description = "관리자가 주문을 취소합니다")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable Long orderId) {
        log.info("관리자 주문 취소 요청: 주문ID={}", orderId);
        OrderResponse response = orderService.cancelOrderByAdmin(orderId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/summary")
    @Operation(summary = "주문 통계 조회", description = "주문 통계를 조회합니다")
    public ResponseEntity<OrderSummaryResponse> getOrderSummary() {
        OrderSummaryResponse response = orderService.getOrderSummary();
        return ResponseEntity.ok(response);
    }
}