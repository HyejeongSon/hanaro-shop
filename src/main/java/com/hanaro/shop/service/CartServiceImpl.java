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
        log.info("[CART_ADD] Adding item to cart: memberId={}, productId={}, quantity={}", 
                memberId, request.getProductId(), request.getQuantity());

        Cart cart = getOrCreateCart(memberId);
        Product product = getProductById(request.getProductId());

        if (product.getStockQuantity() < request.getQuantity()) {
            throw new BusinessException(ErrorCode.PRODUCT_OUT_OF_STOCK);
        }

        Optional<CartItem> existingItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId());

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
        log.info("[CART_ADD] Item added to cart successfully: memberId={}, totalItems={}, totalAmount={}", 
                memberId, savedCart.getItems().size(), savedCart.getTotalQuantity());
        return cartMapper.toCartResponse(savedCart);
    }

    @Override
    public CartResponse updateCartItem(Long memberId, Long productId, Integer quantity) {
        log.info("[CART_UPDATE] Updating cart item quantity: memberId={}, productId={}, newQuantity={}", 
                memberId, productId, quantity);

        Cart cart = getCartByMemberIdOrThrow(memberId);
        Product product = getProductById(productId);

        if (product.getStockQuantity() < quantity) {
            throw new BusinessException(ErrorCode.PRODUCT_OUT_OF_STOCK);
        }

        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CART_ITEM_NOT_FOUND));

        cartItem.updateQuantity(quantity);
        cartItem.updateUnitPrice();

        Cart savedCart = cartRepository.save(cart);
        log.info("[CART_UPDATE] Cart item updated successfully: memberId={}, productId={}, quantity={}", 
                memberId, productId, quantity);
        return cartMapper.toCartResponse(savedCart);
    }

    @Override
    public CartResponse removeItemFromCart(Long memberId, Long productId) {
        log.info("[CART_REMOVE] Removing item from cart: memberId={}, productId={}", memberId, productId);

        Cart cart = getCartByMemberIdOrThrow(memberId);
        Product product = getProductById(productId);

        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CART_ITEM_NOT_FOUND));

        cart.removeItem(cartItem);
        cartItemRepository.delete(cartItem);

        Cart savedCart = cartRepository.save(cart);
        log.info("[CART_REMOVE] Item removed from cart successfully: memberId={}, remainingItems={}", 
                memberId, savedCart.getItems().size());
        return cartMapper.toCartResponse(savedCart);
    }

    @Override
    public void clearCart(Long memberId) {
        log.info("[CART_CLEAR] Clearing entire cart: memberId={}", memberId);

        Cart cart = getCartByMemberIdOrThrow(memberId);
        cart.clearItems();
        cartItemRepository.deleteByCartId(cart.getId());
        cartRepository.save(cart);
        log.info("[CART_CLEAR] Cart cleared successfully: memberId={}", memberId);
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
        log.info("[CART_INCREASE] Increasing cart item quantity: memberId={}, productId={}, increaseBy={}", 
                memberId, productId, amount);

        if (amount <= 0) {
            throw new BusinessException(ErrorCode.INVALID_QUANTITY);
        }

        Cart cart = getCartByMemberIdOrThrow(memberId);
        Product product = getProductById(productId);

        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CART_ITEM_NOT_FOUND));

        int newQuantity = cartItem.getQuantity() + amount;
        
        if (product.getStockQuantity() < newQuantity) {
            throw new BusinessException(ErrorCode.PRODUCT_OUT_OF_STOCK);
        }

        cartItem.updateQuantity(newQuantity);
        cartItem.updateUnitPrice();

        Cart savedCart = cartRepository.save(cart);
        log.info("[CART_INCREASE] Cart item increased successfully: memberId={}, productId={}, newQuantity={}", 
                memberId, productId, newQuantity);
        return cartMapper.toCartResponse(savedCart);
    }

    @Override
    public CartResponse decreaseCartItem(Long memberId, Long productId, Integer amount) {
        log.info("[CART_DECREASE] Decreasing cart item quantity: memberId={}, productId={}, decreaseBy={}", 
                memberId, productId, amount);

        if (amount <= 0) {
            throw new BusinessException(ErrorCode.INVALID_QUANTITY);
        }

        Cart cart = getCartByMemberIdOrThrow(memberId);
        Product product = getProductById(productId);

        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId())
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
        if (newQuantity <= 0) {
            log.info("[CART_DECREASE] Item removed due to zero quantity: memberId={}, productId={}", 
                    memberId, productId);
        } else {
            log.info("[CART_DECREASE] Cart item decreased successfully: memberId={}, productId={}, newQuantity={}", 
                    memberId, productId, newQuantity);
        }
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
        return productRepository.findByIdAndIsDeletedFalse(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
    }
}