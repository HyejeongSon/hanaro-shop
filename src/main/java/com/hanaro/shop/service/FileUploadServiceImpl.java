package com.hanaro.shop.service;

import com.hanaro.shop.dto.UploadResultDTO;
import com.hanaro.shop.exception.BusinessException;
import com.hanaro.shop.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class FileUploadServiceImpl implements FileUploadService {

    @Value("${upload.path}")
    private String uploadPath;

    @PostConstruct
    private void initUploadPath() {
        // 상대경로인 경우 프로젝트 루트 기준으로 절대경로 변환
        if (!Paths.get(uploadPath).isAbsolute()) {
            uploadPath = System.getProperty("user.dir") + File.separator + uploadPath;
        }
        log.info("Upload path initialized: {}", uploadPath);
    }

    private static final long MAX_FILE_SIZE = 512 * 1024; // 512KB
    private static final long MAX_TOTAL_SIZE = 3 * 1024 * 1024; // 3MB
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList("image/jpeg", "image/jpg", "image/png", "image/gif");
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(".jpg", ".jpeg", ".png", ".gif");

    @Override
    public UploadResultDTO uploadFile(MultipartFile file) {
        log.info("Uploading file: {}", file.getOriginalFilename());

        validateImageFile(file);

        // 파일 크기는 Spring Boot servlet.multipart 설정에서 자동 검증
        // validateFileSize(file);

        try {
            String uuid = UUID.randomUUID().toString();
            String originalFilename = file.getOriginalFilename();
            String savedFileName = uuid + "_" + originalFilename;
            
            // 날짜별 디렉토리 생성
            String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String uploadDir = uploadPath + File.separator + dateStr.replace("/", File.separator);
            
            createDirectoriesIfNotExists(uploadDir);

            // 업로드 파일 저장 (/upload)
            Path savePath = Paths.get(uploadDir, savedFileName);
            file.transferTo(savePath);

            boolean isImage = false;
            
            UploadResultDTO.UploadResultDTOBuilder resultBuilder = UploadResultDTO.builder()
                    .uuid(uuid)
                    .fileName(originalFilename)
                    .img(isImage)
                    .fileSize(file.getSize())
                    .filePath("/" + dateStr + "/" + savedFileName);

            // 이미지 파일인 경우 썸네일 생성
            if (Files.probeContentType(savePath).startsWith("image")) {
                isImage = true;
                String thumbnailFileName = "s_" + savedFileName;
                File thumbFile = new File(uploadDir, thumbnailFileName);
                Thumbnailator.createThumbnail(savePath.toFile(), thumbFile, 200, 200);
                log.info("Thumbnail created: {}", thumbFile.getName());

                // 썸네일 정보 추가
                UploadResultDTO thumbnailInfo = UploadResultDTO.builder()
                        .uuid(uuid)
                        .fileName(thumbnailFileName)
                        .img(true)
                        .fileSize(thumbFile.length())
                        .filePath("/" + dateStr + "/" + thumbnailFileName)
                        .build();

                resultBuilder.thumbnail(thumbnailInfo);
            }

            resultBuilder.img(isImage);
            log.info("File uploaded successfully: {}", savedFileName);
            
            return resultBuilder.build();

        } catch (IOException e) {
            log.error("Failed to upload file: {}", file.getOriginalFilename(), e);
            throw new BusinessException(ErrorCode.FILE_UPLOAD_ERROR);
        }
    }

    @Override
    public List<UploadResultDTO> uploadFiles(List<MultipartFile> files) {
        log.info("Uploading multiple files: count={}", files.size());
        
        // 전체 파일 크기는 Spring Boot servlet.multipart 설정에서 자동 검증
        // validateTotalFileSize(files);
        
        List<UploadResultDTO> results = new ArrayList<>();
        for (MultipartFile file : files) {
            results.add(uploadFile(file));
        }
        
        return results;
    }

    @Override
    public void deleteFile(String fileName) {
        log.info("Deleting file: {}", fileName);
        
        try {
            // fileName은 날짜 경로를 포함한 상대 경로 (예: /2025/01/15/uuid_filename.jpg)
            // 웹 경로를 물리 경로로 안전하게 변환
            String cleanFileName = fileName.startsWith("/") ? fileName.substring(1) : fileName;
            String physicalPath = cleanFileName.replace("/", File.separator);
            Path filePath = Paths.get(uploadPath, physicalPath);
            Files.deleteIfExists(filePath);
            
            log.info("File deleted successfully: {}", fileName);
            
        } catch (IOException e) {
            log.error("Failed to delete file: {}", fileName, e);
            throw new BusinessException(ErrorCode.FILE_UPLOAD_ERROR);
        }
    }

    @Override
    public void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT);
        }

        // Content-Type 검증
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new BusinessException(ErrorCode.INVALID_FILE_TYPE);
        }

        // 파일 확장자 검증
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT);
        }

        String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BusinessException(ErrorCode.INVALID_FILE_TYPE);
        }
    }

    @Override
    public void validateFileSize(MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException(ErrorCode.FILE_SIZE_EXCEEDED);
        }
    }

    @Override
    public void validateTotalFileSize(List<MultipartFile> files) {
        long totalSize = files.stream().mapToLong(MultipartFile::getSize).sum();
        if (totalSize > MAX_TOTAL_SIZE) {
            throw new BusinessException(ErrorCode.FILE_SIZE_EXCEEDED);
        }
    }

    private void createDirectoriesIfNotExists(String dirPath) throws IOException {
        Path path = Paths.get(dirPath);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
            log.info("Directory created: {}", dirPath);
        }
    }
}