package com.hanaro.shop.mapper;

import com.hanaro.shop.domain.Cart;
import com.hanaro.shop.domain.CartItem;
import com.hanaro.shop.domain.Product;
import com.hanaro.shop.dto.response.CartItemResponse;
import com.hanaro.shop.dto.response.CartResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CartMapper {

    @Value("${server.port:8080}")
    private String serverPort;

    private String getBaseUrl() {
        return "http://localhost:" + serverPort;
    }

    public CartResponse toCartResponse(Cart cart) {
        return CartResponse.builder()
                .id(cart.getId())
                .memberId(cart.getMember().getId())
                .items(toCartItemResponseList(cart.getItems()))
                .totalPrice(cart.getTotalPrice())
                .totalQuantity(cart.getTotalQuantity())
                .isEmpty(cart.isEmpty())
                .createdAt(cart.getCreatedAt())
                .updatedAt(cart.getUpdatedAt())
                .build();
    }

    public List<CartItemResponse> toCartItemResponseList(List<CartItem> cartItems) {
        return cartItems.stream()
                .map(this::toCartItemResponse)
                .collect(Collectors.toList());
    }

    public CartItemResponse toCartItemResponse(CartItem cartItem) {
        Product product = cartItem.getProduct();
        
        return CartItemResponse.builder()
                .id(cartItem.getId())
                .productId(product.getId())
                .productName(product.getName())
                .productDescription(product.getDescription())
                .thumbnailImageUrl(getThumbnailUrl(product))
                .unitPrice(cartItem.getUnitPrice())
                .quantity(cartItem.getQuantity())
                .totalPrice(cartItem.getTotalPrice())
                .stockQuantity(product.getStockQuantity())
                .createdAt(cartItem.getCreatedAt())
                .updatedAt(cartItem.getUpdatedAt())
                .build();
    }

    private String getThumbnailUrl(Product product) {
        String baseUrl = getBaseUrl();
        
        return product.getImages().stream()
                .filter(image -> image.getIsThumbnail())
                .findFirst()
                .map(image -> baseUrl + "/upload" + image.getFilePath())
                .orElse(null);
    }
}