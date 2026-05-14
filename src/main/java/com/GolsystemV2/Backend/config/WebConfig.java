package com.GolsystemV2.Backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

    @Value("${upload.path:uploads/}")
    private String uploadPath;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // Mapeo específico para rutas /api/** que usan los controllers
                // Usar allowedOriginPatterns en lugar de allowedOrigins para permitir allowCredentials
                registry.addMapping("/api/**")
                        .allowedOriginPatterns("http://localhost:5173", "http://localhost:5174", "http://localhost:3000")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                        .allowedHeaders("*")
                        .allowCredentials(true)
                        .maxAge(3600);
            }

            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                // Servir archivos subidos estáticamente desde /uploads/**
                registry.addResourceHandler("/uploads/**")
                        .addResourceLocations("file:" + uploadPath);
            }
        };
    }
}
