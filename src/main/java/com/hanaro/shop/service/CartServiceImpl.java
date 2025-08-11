package com.hanaro.shop.service;

import com.hanaro.shop.domain.Cart;
import com.hanaro.shop.domain.CartItem;
import com.hanaro.shop.domain.Member;
import com.hanaro.shop.domain.Product;
import com.hanaro.shop.dto.request.CartItemRequest;
import com.hanaro.shop.dto.response.CartResponse;
import com.hanaro.shop.exception.BusinessException;
import com.hanaro.shop.exception.ErrorCode;
import com.hanaro.shop.mapper.CartMapper;
import com.hanaro.shop.repository.CartItemRepository;
import com.hanaro.shop.repository.CartRepository;
import com.hanaro.shop.repository.MemberRepository;
import com.hanaro.shop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final CartMapper cartMapper;

    @Override
    public CartResponse getCart(Long memberId) {
        Cart cart = getOrCreateCart(memberId);
        return cartMapper.toCartResponse(cart);
    }

    @Override
    public CartResponse addItemToCart(Long memberId, CartItemRequest request) {
        log.info("장바구니에 상품 추가: memberId={}, productId={}, quantity={}", 
                memberId, request.getProductId(), request.getQuantity());

        Cart cart = getOrCreateCart(memberId);
        Product product = getProductById(request.getProductId());

        if (product.getStockQuantity() < request.getQuantity()) {
            throw new BusinessException(ErrorCode.PRODUCT_OUT_OF_STOCK);
        }

        Optional<CartItem> existingItem = cartItemRepository.findByCartAndProduct(cart, product);

        if (existingItem.isPresent()) {
            CartItem cartItem = existingItem.get();
            int newQuantity = cartItem.getQuantity() + request.getQuantity();
            
            if (product.getStockQuantity() < newQuantity) {
                throw new BusinessException(ErrorCode.PRODUCT_OUT_OF_STOCK);
            }
            
            cartItem.updateQuantity(newQuantity);
            cartItem.updateUnitPrice();
        } else {
            CartItem newCartItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.getQuantity())
                    .unitPrice(product.getPrice())
                    .build();
            
            cart.addItem(newCartItem);
            cartItemRepository.save(newCartItem);
        }

        Cart savedCart = cartRepository.save(cart);
        return cartMapper.toCartResponse(savedCart);
    }

    @Override
    public CartResponse updateCartItem(Long memberId, Long productId, Integer quantity) {
        log.info("장바구니 상품 수량 변경: memberId={}, productId={}, quantity={}", 
                memberId, productId, quantity);

        Cart cart = getCartByMemberIdOrThrow(memberId);
        Product product = getProductById(productId);

        if (product.getStockQuantity() < quantity) {
            throw new BusinessException(ErrorCode.PRODUCT_OUT_OF_STOCK);
        }

        CartItem cartItem = cartItemRepository.findByCartAndProduct(cart, product)
                .orElseThrow(() -> new BusinessException(ErrorCode.CART_ITEM_NOT_FOUND));

        cartItem.updateQuantity(quantity);
        cartItem.updateUnitPrice();

        Cart savedCart = cartRepository.save(cart);
        return cartMapper.toCartResponse(savedCart);
    }

    @Override
    public CartResponse removeItemFromCart(Long memberId, Long productId) {
        log.info("장바구니에서 상품 제거: memberId={}, productId={}", memberId, productId);

        Cart cart = getCartByMemberIdOrThrow(memberId);
        Product product = getProductById(productId);

        CartItem cartItem = cartItemRepository.findByCartAndProduct(cart, product)
                .orElseThrow(() -> new BusinessException(ErrorCode.CART_ITEM_NOT_FOUND));

        cart.removeItem(cartItem);
        cartItemRepository.delete(cartItem);

        Cart savedCart = cartRepository.save(cart);
        return cartMapper.toCartResponse(savedCart);
    }

    @Override
    public void clearCart(Long memberId) {
        log.info("장바구니 전체 비우기: memberId={}", memberId);

        Cart cart = getCartByMemberIdOrThrow(memberId);
        cart.clearItems();
        cartItemRepository.deleteByCartId(cart.getId());
        cartRepository.save(cart);
    }

    @Override
    @Transactional(readOnly = true)
    public int getCartItemTypes(Long memberId) {
        return cartRepository.findByMemberId(memberId)
                .map(cart -> cart.getItems().size())
                .orElse(0);
    }

    @Override
    public CartResponse increaseCartItem(Long memberId, Long productId, Integer amount) {
        log.info("장바구니 상품 수량 증가: memberId={}, productId={}, amount={}", 
                memberId, productId, amount);

        if (amount <= 0) {
            throw new BusinessException(ErrorCode.INVALID_QUANTITY);
        }

        Cart cart = getCartByMemberIdOrThrow(memberId);
        Product product = getProductById(productId);

        CartItem cartItem = cartItemRepository.findByCartAndProduct(cart, product)
                .orElseThrow(() -> new BusinessException(ErrorCode.CART_ITEM_NOT_FOUND));

        int newQuantity = cartItem.getQuantity() + amount;
        
        if (product.getStockQuantity() < newQuantity) {
            throw new BusinessException(ErrorCode.PRODUCT_OUT_OF_STOCK);
        }

        cartItem.updateQuantity(newQuantity);
        cartItem.updateUnitPrice();

        Cart savedCart = cartRepository.save(cart);
        return cartMapper.toCartResponse(savedCart);
    }

    @Override
    public CartResponse decreaseCartItem(Long memberId, Long productId, Integer amount) {
        log.info("장바구니 상품 수량 감소: memberId={}, productId={}, amount={}", 
                memberId, productId, amount);

        if (amount <= 0) {
            throw new BusinessException(ErrorCode.INVALID_QUANTITY);
        }

        Cart cart = getCartByMemberIdOrThrow(memberId);
        Product product = getProductById(productId);

        CartItem cartItem = cartItemRepository.findByCartAndProduct(cart, product)
                .orElseThrow(() -> new BusinessException(ErrorCode.CART_ITEM_NOT_FOUND));

        int newQuantity = cartItem.getQuantity() - amount;
        
        if (newQuantity <= 0) {
            // 수량이 0 이하가 되면 상품을 장바구니에서 제거
            cart.removeItem(cartItem);
            cartItemRepository.delete(cartItem);
        } else {
            cartItem.updateQuantity(newQuantity);
            cartItem.updateUnitPrice();
        }

        Cart savedCart = cartRepository.save(cart);
        return cartMapper.toCartResponse(savedCart);
    }

    private Cart getOrCreateCart(Long memberId) {
        return cartRepository.findByMemberIdWithItems(memberId)
                .orElseGet(() -> createNewCart(memberId));
    }

    private Cart createNewCart(Long memberId) {
        Member member = getMemberById(memberId);
        Cart newCart = Cart.builder()
                .member(member)
                .build();
        return cartRepository.save(newCart);
    }

    private Cart getCartByMemberIdOrThrow(Long memberId) {
        return cartRepository.findByMemberIdWithItems(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CART_NOT_FOUND));
    }

    private Member getMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
    }

    private Product getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
    }
}