package com.hanaro.shop.service;

import com.hanaro.shop.dto.UploadResultDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileUploadService {
    
    UploadResultDTO uploadFile(MultipartFile file);
    
    List<UploadResultDTO> uploadFiles(List<MultipartFile> files);
    
    void deleteFile(String fileName);
    
    void validateImageFile(MultipartFile file);
    
    void validateFileSize(MultipartFile file);
    
    void validateTotalFileSize(List<MultipartFile> files);
}