package com.yaduvanshi_brothers.api.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:3001","https://college-management-eight.vercel.app","https://college-management-0127cs211009-gmailcoms-projects.vercel.app","https://college-management-git-main-0127cs211009-gmailcoms-projects.vercel.app","https://college-management-brrb.onrender.com"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE","PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "Accept", "Origin", "Cache-Control", "Pragma"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Allow specific headers for multipart data
        configuration.addExposedHeader("Content-Disposition");

        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
