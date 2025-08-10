package com.hanaro.shop.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "파일 업로드 결과")
public class UploadResultDTO {

    @Schema(description = "UUID", example = "123e4567-e89b-12d3-a456-426614174000")
    private String uuid;

    @Schema(description = "원본 파일명", example = "product.jpg")
    private String fileName;

    @Schema(description = "이미지 파일 여부", example = "true")
    private boolean img;

    @Schema(description = "파일 크기", example = "102400")
    private long fileSize;

    @Schema(description = "파일 경로", example = "/upload/2025/01/15")
    private String filePath;
}