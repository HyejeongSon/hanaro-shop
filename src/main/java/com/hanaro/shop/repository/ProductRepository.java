package com.hanaro.shop.repository;

import com.hanaro.shop.domain.Product;
import com.hanaro.shop.domain.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // 삭제되지 않은 상품 조회
    Optional<Product> findByIdAndIsDeletedFalse(Long id);
    
    // 삭제되지 않은 모든 상품 조회
    List<Product> findByIsDeletedFalse();
    
    // 삭제되지 않은 상품 페이징 조회
    Page<Product> findByIsDeletedFalse(Pageable pageable);
    
    // 카테고리별 상품 조회 (삭제되지 않은 상품만)
    List<Product> findByCategoryAndIsDeletedFalse(ProductCategory category);
    
    // 상품명과 설명으로 검색 (삭제되지 않은 상품만)
    @Query("SELECT p FROM Product p WHERE p.isDeleted = false AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Product> findByNameOrDescriptionContainingIgnoreCaseAndIsDeletedFalse(@Param("keyword") String keyword);
    
    // 활성화된 상품만 조회 (삭제되지 않은 상품만)
    List<Product> findByIsActiveTrueAndIsDeletedFalse();
    
    // 카테고리별 활성화된 상품 조회 (삭제되지 않은 상품만)
    List<Product> findByCategoryAndIsActiveTrueAndIsDeletedFalse(ProductCategory category);
    
    // 레거시 메서드들 (deprecated, 삭제된 상품도 포함)
    @Deprecated
    List<Product> findByCategory(ProductCategory category);
    
    @Deprecated
    @Query("SELECT p FROM Product p WHERE " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> findByNameOrDescriptionContainingIgnoreCase(@Param("keyword") String keyword);
    
    @Deprecated
    List<Product> findByIsActiveTrue();
    
    @Deprecated
    List<Product> findByCategoryAndIsActiveTrue(ProductCategory category);
}