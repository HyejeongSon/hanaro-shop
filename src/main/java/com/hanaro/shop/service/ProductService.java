package com.hanaro.shop.service;

import com.hanaro.shop.domain.ProductCategory;
import com.hanaro.shop.dto.request.ProductRequest;
import com.hanaro.shop.dto.response.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    
    ProductResponse createProduct(ProductRequest request);

    ProductResponse getProduct(Long productId);
    
    Page<ProductResponse> getProducts(Pageable pageable);
    
    List<ProductResponse> getProductsByCategory(ProductCategory category);
    
    ProductResponse updateProduct(Long productId, ProductRequest request);
    
    ProductResponse updateStockQuantity(Long productId, Integer quantity);
    
    ProductResponse updateProductStatus(Long productId, Boolean active);
    
    void deleteProduct(Long productId);
    
    List<ProductResponse> searchProducts(String keyword);
}