package com.hanaro.shop.service;

import com.hanaro.shop.domain.*;
import com.hanaro.shop.dto.request.OrderCreateRequest;
import com.hanaro.shop.dto.request.OrderSearchRequest;
import com.hanaro.shop.dto.response.OrderResponse;
import com.hanaro.shop.dto.response.OrderSummaryResponse;
import com.hanaro.shop.exception.BusinessException;
import com.hanaro.shop.exception.ErrorCode;
import com.hanaro.shop.mapper.OrderMapper;
import com.hanaro.shop.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {
    
    private final OrderRepository orderRepository;
    private final DeliveryRepository deliveryRepository;
    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderMapper orderMapper;
    
    @Override
    @Transactional
    public OrderResponse createOrderFromCart(String memberEmail, OrderCreateRequest request) {
        log.info("선택 장바구니 주문 생성 시작: {}, 선택된 아이템: {}", memberEmail, request.getCartItemIds());
        
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        
        // 선택된 장바구니 아이템들 조회
        List<CartItem> selectedCartItems = cartItemRepository.findByIdInWithProduct(request.getCartItemIds());
        
        if (selectedCartItems.isEmpty()) {
            throw new BusinessException(ErrorCode.CART_ITEM_NOT_FOUND);
        }
        
        // 요청된 아이템 개수와 실제 조회된 아이템 개수 비교
        if (selectedCartItems.size() != request.getCartItemIds().size()) {
            throw new BusinessException(ErrorCode.CART_ITEM_NOT_FOUND);
        }
        
        // 사용자의 장바구니인지 확인
        Cart userCart = cartRepository.findByMemberId(member.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CART_NOT_FOUND));

        // 사용자의 장바구니 아이템 인지 확인
        for (CartItem cartItem : selectedCartItems) {
            if (!cartItem.getCart().getId().equals(userCart.getId())) {
                throw new BusinessException(ErrorCode.ACCESS_DENIED);
            }
        }
        
        // 주문 생성
        Order order = Order.builder()
                .member(member)
                .orderNumber(generateOrderNumber())
                .status(OrderStatus.ORDERED)
                .deliveryAddress(request.getDeliveryAddress())
                .deliveryPhone(request.getDeliveryPhone())
                .deliveryRequest(request.getDeliveryRequest())
                .build();
        
        // 선택된 주문 항목 생성 (재고 확인 포함)
        for (CartItem cartItem : selectedCartItems) {
            Product product = cartItem.getProduct();
            
            // 1. 상품 활성화 상태 확인
            if (product.getIsActive() == null || !product.getIsActive()) {
                throw new BusinessException(ErrorCode.PRODUCT_INACTIVE);
            }
            
            // 2. 재고 부족 확인 및 차감은 OrderItem.createOrderItem()에서 처리
            OrderItem orderItem = OrderItem.createOrderItem(product, cartItem.getQuantity());
            order.addOrderItem(orderItem);
        }
        
        // 총액 계산 및 설정
        order.calculateTotalAmount();
        
        // 배송 정보 생성
        Delivery delivery = Delivery.builder()
                .address(request.getDeliveryAddress())
                .phone(request.getDeliveryPhone())
                .recipientName(request.getRecipientName())
                .deliveryRequest(request.getDeliveryRequest())
                .status(DeliveryStatus.PENDING)
                .build();
        
        order.setDelivery(delivery);
        
        Order savedOrder = orderRepository.save(order);
        
        // 선택된 장바구니 아이템들만 삭제 (실제 조회된 아이템들만)
        for (CartItem cartItem : selectedCartItems) {
            cartItemRepository.deleteById(cartItem.getId());
        }
        
        log.info("선택 장바구니 주문 생성 완료: 주문번호={}, 총액={}", 
                savedOrder.getOrderNumber(), savedOrder.getTotalAmount());
        
        return orderMapper.toOrderResponseForUser(savedOrder);
    }

    @Override
    public OrderResponse getOrderByUser(String memberEmail, Long orderId) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
        
        // 본인 주문인지 확인
        if (!order.getMember().getId().equals(member.getId())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }
        
        return orderMapper.toOrderResponseForUser(order);
    }

    @Override
    public Page<OrderResponse> getOrdersByUser(String memberEmail, int page, int size) {
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Order> orders = orderRepository.findByMemberOrderByCreatedAtDesc(member, pageable);
        
        return orders.map(orderMapper::toOrderResponseForUser);
    }
    
    @Override
    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
        
        return orderMapper.toOrderResponse(order);
    }

    /**
     * 관리자: 주문 ID로 주문 조회
     */
    @Override
    public List<OrderResponse> getAllOrders() {
        List<Order> orders = orderRepository.findAll(Sort.by("createdAt").descending());
        
        return orders.stream()
                .map(orderMapper::toOrderResponse)
                .collect(Collectors.toList());
    }

    /**
     * 관리자: 조건 검색(주문번호, 회원 이메일, 상태)
     */
    @Override
    public Page<OrderResponse> searchOrders(OrderSearchRequest request) {
        Pageable pageable = PageRequest.of(
                request.getPage(), 
                request.getSize(),
                Sort.by("createdAt").descending()
        );
        
        Page<Order> orders = orderRepository.findOrdersWithSearch(
                request.getOrderNumber(),
                request.getMemberEmail(),
                request.getStatus(),
                pageable
        );
        
        return orders.map(orderMapper::toOrderResponse);
    }

    private Order cancelOrderInternal(Order order) {
        if (!order.canCancel()) {
            throw new BusinessException(ErrorCode.ORDER_CANNOT_CANCEL);
        }

        order.cancelOrder(); // 주문 상태 변경 (재고 복원 포함)

        return order;
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(String memberEmail, Long orderId) {
        log.info("주문 취소 요청: 사용자={}, 주문ID={}", memberEmail, orderId);
        
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
        
        // 본인 주문인지 확인
        if (!order.getMember().getId().equals(member.getId())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        Order canceledOrder = cancelOrderInternal(order);
        
        log.info("주문 취소 완료: 주문번호={}", canceledOrder.getOrderNumber());
        return orderMapper.toOrderResponseForUser(canceledOrder);
    }

    /**
     * 관리자: 주문 취소
     */
    @Override
    @Transactional
    public OrderResponse cancelOrderByAdmin(Long orderId) {
        log.info("관리자 주문 취소 요청: 주문ID={}", orderId);
        
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        Order canceledOrder = cancelOrderInternal(order);
        
        log.info("관리자 주문 취소 완료: 주문번호={}", canceledOrder.getOrderNumber());
        return orderMapper.toOrderResponse(canceledOrder);
    }

    /**
     * 관리자: 주문 통계 조회
     */
    @Override
    public OrderSummaryResponse getOrderSummary() {
        List<Order> allOrders = orderRepository.findAll();
        
        // 전체 주문 통계 (유효 주문만)
        long totalCount = allOrders.stream()
                .filter(order -> order.getStatus() == OrderStatus.ORDERED)
                .count();
        BigDecimal totalAmount = allOrders.stream()
                .filter(order -> order.getStatus() == OrderStatus.ORDERED) // 완료된 주문만
                .map(Order::getTotalAmount)
                .filter(amount -> amount != null) // null 체크
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // 오늘 주문 통계
        LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime todayEnd = todayStart.plusDays(1);
        
        List<Order> todayOrders = allOrders.stream()
                .filter(order -> order.getCreatedAt().isAfter(todayStart) && 
                               order.getCreatedAt().isBefore(todayEnd))
                .collect(Collectors.toList());
        
        long todayCount = todayOrders.stream()
                .filter(order -> order.getStatus() == OrderStatus.ORDERED)
                .count();
        BigDecimal todayAmount = todayOrders.stream()
                .filter(order -> order.getStatus() == OrderStatus.ORDERED) // 완료된 주문만
                .map(Order::getTotalAmount)
                .filter(amount -> amount != null) // null 체크
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // 상태별 주문 통계
        long orderedCount = allOrders.stream()
                .mapToLong(order -> order.getStatus() == OrderStatus.ORDERED ? 1 : 0)
                .sum();
        long canceledCount = allOrders.stream()
                .mapToLong(order -> order.getStatus() == OrderStatus.CANCELED ? 1 : 0)
                .sum();
        
        return OrderSummaryResponse.builder()
                .totalOrderCount(totalCount)
                .totalOrderAmount(totalAmount)
                .todayOrderCount(todayCount)
                .todayOrderAmount(todayAmount)
                .orderedOrderCount(orderedCount)
                .canceledOrderCount(canceledCount)
                .build();
    }

    private String generateOrderNumber() {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uuid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "ORD" + date + uuid;
    }
}