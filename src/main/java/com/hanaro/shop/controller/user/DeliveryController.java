package com.hanaro.shop.controller.user;

import com.hanaro.shop.dto.response.DeliveryResponse;
import com.hanaro.shop.service.DeliveryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/deliveries")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
@Tag(name = "Delivery - 배송", description = "배송 조회 API")
public class DeliveryController {
    
    private final DeliveryService deliveryService;
    
    @GetMapping("/order/{orderId}")
    @Operation(summary = "주문 배송 조회", description = "자신의 주문 배송 상태를 조회합니다")
    public ResponseEntity<DeliveryResponse> getDeliveryByOrder(
            @PathVariable Long orderId,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        DeliveryResponse response = deliveryService.getDeliveryByOrderId(orderId, userDetails.getUsername());
        return ResponseEntity.ok(response);
    }
    
}