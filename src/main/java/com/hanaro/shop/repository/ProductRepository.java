package com.hanaro.shop.repository;

import com.hanaro.shop.domain.Product;
import com.hanaro.shop.domain.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // 카테고리별 상품 조회
    List<Product> findByCategory(ProductCategory category);
    
    // 상품명과 설명으로 검색
    @Query("SELECT p FROM Product p WHERE " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> findByNameOrDescriptionContainingIgnoreCase(@Param("keyword") String keyword);
    
    // 활성화된 상품만 조회
    List<Product> findByIsActiveTrue();
    
    // 카테고리별 활성화된 상품 조회  
    List<Product> findByCategoryAndIsActiveTrue(ProductCategory category);
}