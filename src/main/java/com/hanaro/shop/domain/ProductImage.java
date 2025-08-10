package com.hanaro.shop.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "product_images")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ProductImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "파일명은 필수입니다")
    @Column(nullable = false)
    private String fileName;

    @NotBlank(message = "원본 파일명은 필수입니다")
    @Column(nullable = false)
    private String originalFileName;

    @NotBlank(message = "파일 경로는 필수입니다")
    @Column(nullable = false)
    private String filePath;

    @Column
    private String uuid;

    @Column
    private Long fileSize;

    @Builder.Default
    private Boolean isThumbnail = false;

    @Builder.Default
    private Boolean isMainImage = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    public void setAsMainImage() {
        this.isMainImage = true;
    }

    public void unsetAsMainImage() {
        this.isMainImage = false;
    }

    public void setAsThumbnail() {
        this.isThumbnail = true;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}