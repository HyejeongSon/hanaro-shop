package com.hanaro.shop.service;

import com.hanaro.shop.domain.Product;
import com.hanaro.shop.domain.ProductCategory;
import com.hanaro.shop.dto.UploadResultDTO;
import com.hanaro.shop.dto.request.ProductRequest;
import com.hanaro.shop.dto.response.ProductResponse;
import com.hanaro.shop.exception.BusinessException;
import com.hanaro.shop.exception.ErrorCode;
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
        log.info("[PRODUCT_CREATE] Starting product creation: name={}, category={}, price={}, stockQuantity={}", 
                request.getName(), request.getCategory(), request.getPrice(), request.getStockQuantity());
        
        Product product = productMapper.toEntity(request);
        Product savedProduct = productRepository.save(product);
        
        // 이미지 업로드 처리
        List<MultipartFile> images = request.getImages();
        if (images != null && !images.isEmpty()) {
            List<UploadResultDTO> uploadResults = fileService.uploadFiles(images);
            
            for (int i = 0; i < uploadResults.size(); i++) {
                UploadResultDTO result = uploadResults.get(i);
                boolean isMainImage = i == 0; // 첫 번째 이미지를 메인으로 설정
                
                // 원본 이미지 저장
                savedProduct.addImage(com.hanaro.shop.domain.ProductImage.builder()
                        .fileName(result.getUuid() + "_" + result.getFileName())
                        .originalFileName(result.getFileName())
                        .filePath(result.getFilePath())
                        .uuid(result.getUuid())
                        .fileSize(result.getFileSize())
                        .isThumbnail(false)
                        .isMainImage(isMainImage)
                        .build());
                
                // 썸네일 이미지 저장
                if (result.getThumbnail() != null) {
                    UploadResultDTO thumbnail = result.getThumbnail();
                    savedProduct.addImage(com.hanaro.shop.domain.ProductImage.builder()
                            .fileName(thumbnail.getFileName())
                            .originalFileName("s_" + result.getFileName())
                            .filePath(thumbnail.getFilePath())
                            .uuid(thumbnail.getUuid())
                            .fileSize(thumbnail.getFileSize())
                            .isThumbnail(true)
                            .isMainImage(false)
                            .build());
                }
            }
            
            savedProduct = productRepository.save(savedProduct);
        }
        
        log.info("[PRODUCT_CREATE] Product created successfully: id={}, name={}, imageCount={}", 
                savedProduct.getId(), savedProduct.getName(), savedProduct.getImages().size());
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
        log.info("[PRODUCT_UPDATE] Starting product update: id={}, name={}, price={}", 
                productId, request.getName(), request.getPrice());
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
        
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
                
                // 원본 이미지 저장
                product.addImage(com.hanaro.shop.domain.ProductImage.builder()
                        .fileName(result.getUuid() + "_" + result.getFileName())
                        .originalFileName(result.getFileName())
                        .filePath(result.getFilePath())
                        .uuid(result.getUuid())
                        .fileSize(result.getFileSize())
                        .isThumbnail(false)
                        .isMainImage(isMainImage)
                        .build());
                
                // 썸네일 이미지 저장
                if (result.getThumbnail() != null) {
                    UploadResultDTO thumbnail = result.getThumbnail();
                    product.addImage(com.hanaro.shop.domain.ProductImage.builder()
                            .fileName(thumbnail.getFileName())
                            .originalFileName("s_" + result.getFileName())
                            .filePath(thumbnail.getFilePath())
                            .uuid(thumbnail.getUuid())
                            .fileSize(thumbnail.getFileSize())
                            .isThumbnail(true)
                            .isMainImage(false)
                            .build());
                }
            }
        }
        
        Product updatedProduct = productRepository.save(product);
        log.info("[PRODUCT_UPDATE] Product updated successfully: id={}, name={}, imageCount={}", 
                updatedProduct.getId(), updatedProduct.getName(), updatedProduct.getImages().size());
        return productMapper.toResponse(updatedProduct);
    }

    @Override
    public ProductResponse updateStockQuantity(Long productId, Integer quantity) {
        log.info("[PRODUCT_STOCK] Starting stock quantity update: productId={}, newQuantity={}", productId, quantity);
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
        
        product.updateStockQuantity(quantity);
        
        Product updatedProduct = productRepository.save(product);
        log.info("[PRODUCT_STOCK] Stock quantity updated successfully: id={}, oldQuantity={}, newQuantity={}", 
                updatedProduct.getId(), product.getStockQuantity(), updatedProduct.getStockQuantity());
        return productMapper.toResponse(updatedProduct);
    }

    @Override
    public void deleteProduct(Long productId) {
        log.info("[PRODUCT_DELETE] Starting product deletion: id={}", productId);
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
        
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
        log.info("[PRODUCT_DELETE] Product deleted successfully: id={}, name={}, imageCount={}", 
                productId, product.getName(), product.getImages().size());
    }

    @Override
    public ProductResponse updateProductStatus(Long productId, Boolean active) {
        log.info("[PRODUCT_STATUS] Starting product status update: productId={}, newActive={}", productId, active);
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));
        
        if (active) {
            product.activate();
        } else {
            product.deactivate();
        }
        
        Product updatedProduct = productRepository.save(product);
        log.info("[PRODUCT_STATUS] Product status updated successfully: id={}, oldActive={}, newActive={}", 
                updatedProduct.getId(), !active, active);
        return productMapper.toResponse(updatedProduct);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> searchProducts(String keyword) {
        List<Product> products = productRepository.findByNameOrDescriptionContainingIgnoreCase(keyword);
        return productMapper.toResponseList(products);
    }

}