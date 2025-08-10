package com.hanaro.shop.service;

import com.hanaro.shop.dto.request.CartItemRequest;
import com.hanaro.shop.dto.response.CartResponse;
import com.hanaro.shop.dto.response.CartSummaryResponse;

public interface CartService {
    
    CartResponse getCart(Long memberId);
    
    CartResponse addItemToCart(Long memberId, CartItemRequest request);
    
    CartResponse updateCartItem(Long memberId, Long productId, Integer quantity);
    
    CartResponse removeItemFromCart(Long memberId, Long productId);
    
    void clearCart(Long memberId);
    
    int getCartItemCount(Long memberId);
    
    // 새로 추가되는 API들
    int getCartItemTypes(Long memberId);
    
    CartSummaryResponse getCartSummary(Long memberId);
    
    CartResponse increaseCartItem(Long memberId, Long productId, Integer amount);
    
    CartResponse decreaseCartItem(Long memberId, Long productId, Integer amount);
}