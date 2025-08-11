package com.hanaro.shop.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateRequest {
    
    @NotEmpty(message = "선택된 장바구니 상품이 필요합니다")
    private List<Long> cartItemIds;
    
    @NotBlank(message = "배송지 주소는 필수입니다")
    private String deliveryAddress;
    
    private String deliveryPhone;
    
    private String deliveryRequest;
    
    @NotBlank(message = "수령인 이름은 필수입니다")
    private String recipientName;
}