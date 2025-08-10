package com.hanaro.shop.service;

import com.hanaro.shop.domain.Product;
import com.hanaro.shop.domain.ProductCategory;
import com.hanaro.shop.dto.UploadResultDTO;
import com.hanaro.shop.dto.request.ProductRequest;
import com.hanaro.shop.dto.response.ProductResponse;
import com.hanaro.shop.mapper.ProductMapper;
import com.hanaro.shop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final FileUploadService fileService;

    @Override
    public ProductResponse createProduct(ProductRequest request) {
        log.info("Creating product: name={}, category={}", request.getName(), request.getCategory());
        
        Product product = productMapper.toEntity(request);
        Product savedProduct = productRepository.save(product);
        
        // 이미지 업로드 처리
        List<MultipartFile> images = request.getImages();
        if (images != null && !images.isEmpty()) {
            List<UploadResultDTO> uploadResults = fileService.uploadFiles(images);
            
            for (int i = 0; i < uploadResults.size(); i++) {
                UploadResultDTO result = uploadResults.get(i);
                boolean isMainImage = i == 0; // 첫 번째 이미지를 메인으로 설정
                
                savedProduct.addImage(com.hanaro.shop.domain.ProductImage.builder()
                        .fileName(result.getUuid() + "_" + result.getFileName())
                        .originalFileName(result.getFileName())
                        .filePath(result.getFilePath())
                        .uuid(result.getUuid())
                        .fileSize(result.getFileSize())
                        .isThumbnail(false)
                        .isMainImage(isMainImage)
                        .build());
            }
            
            savedProduct = productRepository.save(savedProduct);
        }
        
        log.info("Product created successfully: id={}", savedProduct.getId());
        return productMapper.toResponse(savedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductResponse> getProduct(Long productId) {
        return productRepository.findById(productId)
                .map(productMapper::toResponse);
    }


    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(productMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getProductsByCategory(ProductCategory category) {
        List<Product> products = productRepository.findByCategory(category);
        return productMapper.toResponseList(products);
    }


    @Override
    public ProductResponse updateProduct(Long productId, ProductRequest request) {
        log.info("Updating product: id={}", productId);
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productId));
        
        productMapper.updateEntity(product, request);
        
        // 이미지가 있는 경우 기존 이미지 삭제 후 새로 추가
        List<MultipartFile> images = request.getImages();
        if (images != null && !images.isEmpty()) {
            // 1. 기존 물리 파일들 삭제
            product.getImages().forEach(image -> {
                try {
                    fileService.deleteFile(image.getFilePath());
                } catch (RuntimeException e) {
                    log.warn("Failed to delete existing image: {}", image.getFileName(), e);
                }
            });
            
            // 2. DB에서 기존 이미지 정보 삭제
            product.clearImages();
            
            // 3. 새 이미지 업로드 및 추가
            List<UploadResultDTO> uploadResults = fileService.uploadFiles(images);
            
            for (int i = 0; i < uploadResults.size(); i++) {
                UploadResultDTO result = uploadResults.get(i);
                boolean isMainImage = i == 0; // 첫 번째를 메인으로
                
                product.addImage(com.hanaro.shop.domain.ProductImage.builder()
                        .fileName(result.getUuid() + "_" + result.getFileName())
                        .originalFileName(result.getFileName())
                        .filePath(result.getFilePath())
                        .uuid(result.getUuid())
                        .fileSize(result.getFileSize())
                        .isThumbnail(false)
                        .isMainImage(isMainImage)
                        .build());
            }
        }
        
        Product updatedProduct = productRepository.save(product);
        log.info("Product updated successfully: id={}", updatedProduct.getId());
        return productMapper.toResponse(updatedProduct);
    }

    @Override
    public ProductResponse updateStockQuantity(Long productId, Integer quantity) {
        log.info("Updating stock quantity: productId={}, quantity={}", productId, quantity);
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productId));
        
        product.updateStockQuantity(quantity);
        
        Product updatedProduct = productRepository.save(product);
        log.info("Stock quantity updated successfully: id={}, newQuantity={}", 
                updatedProduct.getId(), updatedProduct.getStockQuantity());
        return productMapper.toResponse(updatedProduct);
    }

    @Override
    public void deleteProduct(Long productId) {
        log.info("Deleting product: id={}", productId);
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productId));
        
        // 1. 먼저 연관된 물리 파일들 삭제
        product.getImages().forEach(image -> {
            try {
                fileService.deleteFile(image.getFilePath());
                log.info("Deleted physical file: {}", image.getFileName());
            } catch (RuntimeException e) {
                log.warn("Failed to delete physical file: {}", image.getFileName(), e);
            }
        });
        
        // 2. DB에서 상품(및 연관 이미지) 삭제  
        productRepository.delete(product);
        log.info("Product deleted successfully: id={}", productId);
    }

    @Override
    public ProductResponse updateProductStatus(Long productId, Boolean active) {
        log.info("Updating product status: productId={}, active={}", productId, active);
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + productId));
        
        if (active) {
            product.activate();
        } else {
            product.deactivate();
        }
        
        Product updatedProduct = productRepository.save(product);
        log.info("Product status updated successfully: id={}, active={}", updatedProduct.getId(), active);
        return productMapper.toResponse(updatedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> searchProducts(String keyword) {
        List<Product> products = productRepository.findByNameOrDescriptionContainingIgnoreCase(keyword);
        return productMapper.toResponseList(products);
    }

}