package com.hanaro.shop.service;

import com.hanaro.shop.domain.Product;
import com.hanaro.shop.domain.ProductCategory;
import com.hanaro.shop.domain.ProductImage;
import com.hanaro.shop.repository.ProductRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

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

import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
class ProductServiceImplTest {

    @Autowired
    private ProductRepository productRepository;

    // 테스트용 업로드 경로 설정
    private final String uploadPath = "src/main/resources/static/upload";

    @Test
    @DisplayName("갤럭시 S25 상품 생성")
    void createGalaxyS25Product() {
        // when - 갤럭시 S25 상품 생성
        if (!productRepository.findByNameOrDescriptionContainingIgnoreCase("갤럭시 S25").isEmpty()) {
            log.info("갤럭시 S25 상품이 이미 존재합니다.");
            return;
        }

        // 상품 생성
        Product galaxyS25 = Product.builder()
                .name("갤럭시 S25")
                .description("삼성 갤럭시 S25 최신 스마트폰입니다. 강력한 성능과 뛰어난 카메라 기능을 제공합니다.")
                .price(new BigDecimal("1299000"))
                .stockQuantity(100)
                .category(ProductCategory.ELECTRONICS)
                .isActive(true)
                .build();

        Product savedProduct = productRepository.save(galaxyS25);

        // FileUploadService와 동일한 방식으로 이미지 정보 생성
        String originalFileName = "갤럭시s25.png";
        String uuid = UUID.randomUUID().toString();
        String savedFileName = uuid + "_" + originalFileName;
        
        // 날짜별 디렉토리 경로 생성
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String webPath = "/" + dateStr + "/" + savedFileName;

        // FileUploadServiceImpl과 동일한 방식으로 실제 파일 처리
        Path sourcePath = Paths.get("src", "main", "resources", "static", "origin", originalFileName);
        long fileSize = 0L;
        
        try {
            if (Files.exists(sourcePath)) {
                fileSize = Files.size(sourcePath);
                System.out.println("실제 이미지 파일 크기: " + fileSize + " bytes");
                
                // 날짜별 업로드 디렉토리 생성
                String uploadDir = uploadPath + File.separator + dateStr.replace("/", File.separator);
                Path uploadDirPath = Paths.get(uploadDir);
                if (!Files.exists(uploadDirPath)) {
                    Files.createDirectories(uploadDirPath);
                    System.out.println("업로드 디렉토리 생성: " + uploadDir);
                }
                
                // 원본 이미지를 upload 폴더로 복사
                Path targetPath = Paths.get(uploadDir, savedFileName);
                Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("이미지 파일 복사 완료: " + targetPath);
                
                // 썸네일 생성 (FileUploadServiceImpl과 동일)
                File thumbnailFile = new File(uploadDir, "s_" + savedFileName);
                Thumbnailator.createThumbnail(targetPath.toFile(), thumbnailFile, 200, 200);
                System.out.println("썸네일 생성 완료: " + thumbnailFile.getName());
                
            } else {
                System.out.println("원본 이미지 파일을 찾을 수 없습니다: " + sourcePath);
                fileSize = 160940L; // 기본값 (실제 157KB)
            }
        } catch (IOException e) {
            System.out.println("파일 처리 중 오류 발생: " + e.getMessage());
            fileSize = 160940L; // 기본값 (실제 157KB)
        }

        // 메인 이미지만 생성 (FileUploadServiceImpl과 동일 - 썸네일은 DB에 저장하지 않음)
        ProductImage productImage = ProductImage.builder()
                .fileName(savedFileName)
                .originalFileName(originalFileName)
                .filePath(webPath)
                .uuid(uuid)
                .fileSize(fileSize)
                .isThumbnail(false)
                .isMainImage(true)
                .product(savedProduct)
                .build();

        // 상품에 이미지 추가 (JPA Cascade로 자동 저장)
        savedProduct.addImage(productImage);
        
        // 상품 다시 저장하여 이미지와 함께 영속화
        productRepository.save(savedProduct);

        log.info("갤럭시 S25 상품이 생성되었습니다: ID={}, 이름={}, 가격={}", 
                savedProduct.getId(), savedProduct.getName(), savedProduct.getPrice());

        // then - 검증
        assertTrue(productRepository.existsById(savedProduct.getId()));
        
        Product foundProduct = productRepository.findById(savedProduct.getId()).get();
        assertEquals("갤럭시 S25", foundProduct.getName());
        assertEquals(new BigDecimal("1299000.00"), foundProduct.getPrice());
        assertEquals(Integer.valueOf(100), foundProduct.getStockQuantity());
        assertEquals(ProductCategory.ELECTRONICS, foundProduct.getCategory());
        assertTrue(foundProduct.getIsActive());
        assertNotNull(foundProduct.getCreatedAt());
        assertNotNull(foundProduct.getUpdatedAt());

        // 이미지 검증 (메인 이미지만 DB 저장)
        assertEquals(1, foundProduct.getImages().size());
        
        // 메인 이미지 검증
        ProductImage mainImage = foundProduct.getMainImage();
        assertNotNull(mainImage);
        assertEquals(originalFileName, mainImage.getOriginalFileName());
        assertTrue(mainImage.getIsMainImage());
        assertFalse(mainImage.getIsThumbnail());
        assertEquals(webPath, mainImage.getFilePath());
        assertEquals(savedFileName, mainImage.getFileName());
        assertEquals(uuid, mainImage.getUuid());

        log.info("갤럭시 S25 상품 생성 테스트 완료");
    }
}