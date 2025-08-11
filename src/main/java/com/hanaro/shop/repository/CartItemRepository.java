package com.hanaro.shop.repository;

import com.hanaro.shop.domain.Cart;
import com.hanaro.shop.domain.CartItem;
import com.hanaro.shop.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);

    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.cart.id = :cartId")
    void deleteByCartId(@Param("cartId") Long cartId);

    // cartItemIds 목록에 해당하는 CartItem들을, 각각 연관된 Product까지 한 번에 로딩해서 반환
    @Query("SELECT ci FROM CartItem ci JOIN FETCH ci.product WHERE ci.id IN :cartItemIds")
    List<CartItem> findByIdInWithProduct(@Param("cartItemIds") List<Long> cartItemIds);
}