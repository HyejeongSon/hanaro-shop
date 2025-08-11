package com.hanaro.shop.service;

import com.hanaro.shop.domain.Delivery;
import com.hanaro.shop.domain.Member;
import com.hanaro.shop.domain.Order;
import com.hanaro.shop.dto.response.DeliveryResponse;
import com.hanaro.shop.exception.BusinessException;
import com.hanaro.shop.exception.ErrorCode;
import com.hanaro.shop.mapper.OrderMapper;
import com.hanaro.shop.repository.DeliveryRepository;
import com.hanaro.shop.repository.MemberRepository;
import com.hanaro.shop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DeliveryServiceImpl implements DeliveryService {
    
    private final DeliveryRepository deliveryRepository;
    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final OrderMapper orderMapper;
    
    @Override
    public DeliveryResponse getDeliveryByOrderId(Long orderId, String memberEmail) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
        
        // 본인 주문인지 확인
        if (!order.getMember().getId().equals(member.getId())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }
        
        if (order.getDelivery() == null) {
            throw new BusinessException(ErrorCode.DELIVERY_NOT_FOUND);
        }
        
        return orderMapper.toDeliveryResponse(order.getDelivery());
    }
    
}