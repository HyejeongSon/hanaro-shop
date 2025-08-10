package com.hanaro.shop.mapper;

import com.hanaro.shop.domain.Product;
import com.hanaro.shop.domain.ProductImage;
import com.hanaro.shop.dto.request.ProductRequest;
import com.hanaro.shop.dto.response.ProductImageResponse;
import com.hanaro.shop.dto.response.ProductResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductMapper {

    @Value("${server.port:8080}")
    private String serverPort;
    
    private String getBaseUrl() {
        return "http://localhost:" + serverPort;
    }

    public Product toEntity(ProductRequest request) {
        return Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .category(request.getCategory())
                .build();
    }

    public ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .category(product.getCategory())
                .isActive(product.getIsActive())
                .images(toImageResponseList(product.getImages()))
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    public List<ProductResponse> toResponseList(List<Product> products) {
        return products.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ProductImageResponse toImageResponse(ProductImage image) {
        String baseUrl = getBaseUrl();
        String imageUrl = baseUrl + "/upload" + image.getFilePath();
        
        // 썸네일 URL 생성 (s_ 접두사 추가)
        String thumbnailPath = image.getFilePath();
        String thumbnailUrl = null;
        if (thumbnailPath != null) {
            String[] pathParts = thumbnailPath.split("/");
            if (pathParts.length > 0) {
                String fileName = pathParts[pathParts.length - 1];
                String directory = thumbnailPath.substring(0, thumbnailPath.lastIndexOf("/"));
                thumbnailUrl = baseUrl + "/upload" + directory + "/s_" + fileName;
            }
        }
        
        return ProductImageResponse.builder()
                .id(image.getId())
                .fileName(image.getFileName())
                .originalFileName(image.getOriginalFileName())
                .filePath(image.getFilePath())
                .imageUrl(imageUrl)
                .thumbnailUrl(thumbnailUrl)
                .uuid(image.getUuid())
                .fileSize(image.getFileSize())
                .isThumbnail(image.getIsThumbnail())
                .isMainImage(image.getIsMainImage())
                .createdAt(image.getCreatedAt())
                .build();
    }

    public List<ProductImageResponse> toImageResponseList(List<ProductImage> images) {
        return images.stream()
                .map(this::toImageResponse)
                .collect(Collectors.toList());
    }

    public void updateEntity(Product product, ProductRequest request) {
        product.updateProduct(
                request.getName(),
                request.getDescription(),
                request.getPrice(),
                request.getStockQuantity(),
                request.getCategory()
        );
    }
}