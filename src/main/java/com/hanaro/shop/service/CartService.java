package com.hanaro.shop.service;

import com.hanaro.shop.dto.request.CartItemRequest;
import com.hanaro.shop.dto.response.CartResponse;

public interface CartService {
    
    CartResponse getCart(Long memberId);
    
    CartResponse addItemToCart(Long memberId, CartItemRequest request);
    
    CartResponse updateCartItem(Long memberId, Long productId, Integer quantity);
    
    CartResponse removeItemFromCart(Long memberId, Long productId);
    
    void clearCart(Long memberId);
    
    int getCartItemTypes(Long memberId);
    
    CartResponse increaseCartItem(Long memberId, Long productId, Integer amount);
    
    CartResponse decreaseCartItem(Long memberId, Long productId, Integer amount);
}