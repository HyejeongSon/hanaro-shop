package com.hanaro.shop.mapper;

import com.hanaro.shop.domain.Delivery;
import com.hanaro.shop.domain.Order;
import com.hanaro.shop.domain.OrderItem;
import com.hanaro.shop.dto.response.DeliveryResponse;
import com.hanaro.shop.dto.response.OrderResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {
    
    public OrderResponse toOrderResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .deliveryAddress(order.getDeliveryAddress())
                .deliveryPhone(order.getDeliveryPhone())
                .deliveryRequest(order.getDeliveryRequest())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .memberEmail(order.getMember().getEmail())
                .memberName(order.getMember().getName())
                .orderItems(toOrderItemResponseList(order.getOrderItems()))
                .delivery(order.getDelivery() != null ? toDeliveryResponse(order.getDelivery()) : null)
                .build();
    }
    
    public OrderResponse toOrderResponseForUser(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .orderNumber(order.getOrderNumber())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .deliveryAddress(order.getDeliveryAddress())
                .deliveryPhone(order.getDeliveryPhone())
                .deliveryRequest(order.getDeliveryRequest())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .orderItems(toOrderItemResponseList(order.getOrderItems()))
                .delivery(order.getDelivery() != null ? toDeliveryResponse(order.getDelivery()) : null)
                .build();
    }
    
    public List<OrderResponse.OrderItemResponse> toOrderItemResponseList(List<OrderItem> orderItems) {
        return orderItems.stream()
                .map(this::toOrderItemResponse)
                .collect(Collectors.toList());
    }
    
    public OrderResponse.OrderItemResponse toOrderItemResponse(OrderItem orderItem) {
        String thumbnailUrl = null;
        if (orderItem.getProduct() != null && 
            !orderItem.getProduct().getImages().isEmpty()) {
            thumbnailUrl = orderItem.getProduct().getImages().stream()
                    .filter(image -> image.getIsThumbnail() != null && image.getIsThumbnail())
                    .findFirst()
                    .map(image -> "/upload" + image.getFilePath())
                    .orElse(null);
        }
        
        return OrderResponse.OrderItemResponse.builder()
                .id(orderItem.getId())
                .productId(orderItem.getProduct() != null ? orderItem.getProduct().getId() : null)
                .productName(orderItem.getProductName())
                .quantity(orderItem.getQuantity())
                .unitPrice(orderItem.getUnitPrice())
                .totalPrice(orderItem.getTotalPrice())
                .thumbnailUrl(thumbnailUrl)
                .build();
    }
    
    public DeliveryResponse toDeliveryResponse(Delivery delivery) {
        return DeliveryResponse.builder()
                .id(delivery.getId())
                .address(delivery.getAddress())
                .phone(delivery.getPhone())
                .recipientName(delivery.getRecipientName())
                .deliveryRequest(delivery.getDeliveryRequest())
                .status(delivery.getStatus())
                .trackingNumber(delivery.getTrackingNumber())
                .shippedAt(delivery.getShippedAt())
                .deliveredAt(delivery.getDeliveredAt())
                .createdAt(delivery.getCreatedAt())
                .updatedAt(delivery.getUpdatedAt())
                .build();
    }
}