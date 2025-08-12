package com.hanaro.shop.service;

import com.hanaro.shop.domain.Product;
import com.hanaro.shop.domain.ProductCategory;
import com.hanaro.shop.domain.ProductImage;
import com.hanaro.shop.repository.ProductRepository;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.annotation.Rollback;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
class ProductServiceImplTest {

    @Autowired
    private ProductRepository productRepository;

    // 테스트용 업로드 경로 설정
    private final String uploadPath = "src/main/resources/static/upload";

    /**
     * 더미 상품 생성 공통 메서드
     */
    private void createDummyProduct(String name,
                                    String description,
                                    BigDecimal price,
                                    int stockQuantity,
                                    ProductCategory category,
                                    boolean isActive,
                                    String... imageFileNames) {

        if (!productRepository.findByNameOrDescriptionContainingIgnoreCase(name).isEmpty()) {
            log.info("{} 상품이 이미 존재합니다.", name);
            return;
        }

        // 상품 생성
        Product product = Product.builder()
                .name(name)
                .description(description)
                .price(price)
                .stockQuantity(stockQuantity)
                .category(category)
                .isActive(isActive)
                .build();

        Product savedProduct = productRepository.save(product);

        // UUID, 날짜 폴더명
        String uuid = UUID.randomUUID().toString();
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

        for (int i = 0; i < imageFileNames.length; i++) {
            String originalFileName = imageFileNames[i];
            String savedFileName = uuid + "_" + originalFileName;
            String webPath = "/" + dateStr + "/" + savedFileName;

            // 원본 이미지 복사 + 썸네일 생성
            long fileSize = copyAndGenerateThumbnail(originalFileName, savedFileName, dateStr);

            // 원본 이미지 DB 저장
            ProductImage image = ProductImage.builder()
                    .fileName(savedFileName)
                    .originalFileName(originalFileName)
                    .filePath(webPath)
                    .uuid(uuid)
                    .fileSize(fileSize)
                    .isThumbnail(false)
                    .isMainImage(i == 0) // 첫 번째 이미지를 메인 이미지로
                    .product(savedProduct)
                    .build();
            savedProduct.addImage(image);

            // 썸네일 DB 저장
            String thumbFileName = "s_" + savedFileName;
            String thumbOriginalFileName = "s_" + originalFileName;
            String thumbWebPath = "/" + dateStr + "/" + thumbFileName;
            long thumbSize = getThumbnailFileSize(dateStr, thumbFileName, fileSize);

            ProductImage thumbnailImage = ProductImage.builder()
                    .fileName(thumbFileName)
                    .originalFileName(thumbOriginalFileName)
                    .filePath(thumbWebPath)
                    .uuid(uuid)
                    .fileSize(thumbSize)
                    .isThumbnail(true)
                    .isMainImage(false)
                    .product(savedProduct)
                    .build();
            savedProduct.addImage(thumbnailImage);
        }

        productRepository.save(savedProduct);
        log.info("{} 상품 생성 완료: ID={}", name, savedProduct.getId());
    }

    /**
     * 원본 이미지 복사 + 썸네일 생성
     */
    private long copyAndGenerateThumbnail(String originalFileName, String savedFileName, String dateStr) {
        Path sourcePath = Paths.get("src", "main", "resources", "static", "origin", originalFileName);
        long fileSize = 0L;

        try {
            if (Files.exists(sourcePath)) {
                fileSize = Files.size(sourcePath);

                String uploadDir = uploadPath + File.separator + dateStr.replace("/", File.separator);
                Path uploadDirPath = Paths.get(uploadDir);
                if (!Files.exists(uploadDirPath)) {
                    Files.createDirectories(uploadDirPath);
                }

                Path targetPath = Paths.get(uploadDir, savedFileName);
                Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);

                // 썸네일 생성
                File thumbnailFile = new File(uploadDir, "s_" + savedFileName);
                Thumbnailator.createThumbnail(targetPath.toFile(), thumbnailFile, 200, 200);

            } else {
                log.warn("원본 이미지가 없습니다: {}", sourcePath);
                fileSize = 160000L; // 기본값
            }
        } catch (IOException e) {
            log.error("이미지 처리 중 오류: {}", e.getMessage());
            fileSize = 160000L; // 기본값
        }
        return fileSize;
    }

    /**
     * 썸네일 파일 크기 확인 (없으면 원본의 1/4 크기 추정)
     */
    private long getThumbnailFileSize(String dateStr, String thumbFileName, long originalFileSize) {
        try {
            Path thumbPath = Paths.get(uploadPath, dateStr.replace("/", File.separator), thumbFileName);
            if (Files.exists(thumbPath)) {
                return Files.size(thumbPath);
            }
        } catch (IOException e) {
            log.warn("썸네일 크기 확인 실패: {}", e.getMessage());
        }
        return originalFileSize / 4;
    }

    // ========================= 테스트 메서드 =========================

    @Test
    @DisplayName("갤럭시 S25 생성")
    @Transactional
    @Rollback(false)
    void createGalaxyS25() {
        createDummyProduct(
                "갤럭시 S25",
                "삼성 갤럭시 S25 최신 스마트폰입니다. 강력한 성능과 뛰어난 카메라 기능을 제공합니다.",
                new BigDecimal("1299000"),
                100,
                ProductCategory.ELECTRONICS,
                true,
                "갤럭시s25.png"
        );
    }

    @Test
    @DisplayName("이펙티브 자바 생성")
    @Transactional
    @Rollback(false)
    void createEffectiveJava() {
        createDummyProduct(
                "이펙티브 자바 3/E",
                "Joshua Bloch 저, 자바 개발자 필독서. 고급 자바 프로그래밍 기법과 모범 사례를 담았습니다.",
                new BigDecimal("45000"),
                50,
                ProductCategory.BOOK,
                true,
                "effective_java_front.jpg", "effective_java_back.png"
        );
    }
}