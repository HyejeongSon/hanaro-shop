package com.hanaro.shop.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "상품 이미지 응답")
public class ProductImageResponse {

    @Schema(description = "이미지 ID", example = "1")
    private Long id;

    @Schema(description = "파일명", example = "uuid_image.jpg")
    private String fileName;

    @Schema(description = "원본 파일명", example = "product.jpg")
    private String originalFileName;

    @Schema(description = "파일 경로", example = "/2025/01/15/uuid_image.jpg")
    private String filePath;

    @Schema(description = "이미지 URL", example = "http://localhost:8080/upload/2025/01/15/uuid_image.jpg")
    private String imageUrl;


    @Schema(description = "UUID", example = "123e4567-e89b-12d3-a456-426614174000")
    private String uuid;

    @Schema(description = "파일 크기", example = "102400")
    private Long fileSize;

    @Schema(description = "썸네일 여부", example = "false")
    private Boolean isThumbnail;

    @Schema(description = "메인 이미지 여부", example = "true")
    private Boolean isMainImage;

    @Schema(description = "생성일시")
    private LocalDateTime createdAt;
}