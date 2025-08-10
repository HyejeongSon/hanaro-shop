package com.hanaro.shop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${upload.path}")
    private String uploadPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 크로스 플랫폼 호환 경로 처리
        String absoluteUploadPath;
        if (!Paths.get(uploadPath).isAbsolute()) {
            absoluteUploadPath = System.getProperty("user.dir") + File.separator + uploadPath;
        } else {
            absoluteUploadPath = uploadPath;
        }
        
        // 파일 URL 생성 (OS 관계없이 file:// 형식으로 통일)
        String fileUrl = Paths.get(absoluteUploadPath).toUri().toString();
        
        // 업로드된 파일을 /upload/** URL로 접근할 수 있도록 매핑
        registry.addResourceHandler("/upload/**")
                .addResourceLocations(fileUrl);
    }
}