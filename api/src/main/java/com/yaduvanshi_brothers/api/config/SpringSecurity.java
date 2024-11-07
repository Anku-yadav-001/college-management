package com.yaduvanshi_brothers.api.config;

import com.yaduvanshi_brothers.api.filter.JwtFilter;
import com.yaduvanshi_brothers.api.service.CustomUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
@Configuration
@EnableWebSecurity
public class SpringSecurity {

    @Autowired
    private CustomUserService customUserService;

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        System.out.println("Spring Security configuration starting...");

        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Enable CORS with specific configuration
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/public/**").permitAll() // Allow public endpoints
                        .requestMatchers("/admin/**", "/branch/**", "/faculty/**", "/student/**", "/lectures/**", "/send-mail/**", "/image/**", "/online-classes/**", "/announcements/**", "/one-time-mail/**", "/assignments/**", "/files/**").hasRole("ADMIN")
                        .requestMatchers("/branch/**", "/faculty/**", "/student/**", "/lectures/**", "/send-mail/**", "/image/**", "/online-classes/**", "/announcements/**", "/one-time-mail/**", "/assignments/**", "/files/**").hasRole("FACULTY")
                        .requestMatchers("/student/**", "/lectures/**", "/files/**").hasRole("STUDENT")
                        .anyRequest().authenticated() // Ensure other requests are authenticated
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class) // Add JWT filter
                .build();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserService)
                .passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        System.out.println("Password encoder initialized.");
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // Simplified CORS configuration bean
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(Arrays.asList(
                "https://college-management-eight.vercel.app",
                "https://college-management-0127cs211009-gmailcoms-projects.vercel.app",
                "https://college-management-git-main-0127cs211009-gmailcoms-projects.vercel.app"
        ));
        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        corsConfig.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "Accept", "Origin", "Cache-Control", "Pragma"));
        corsConfig.setAllowCredentials(true);
        corsConfig.addExposedHeader("Content-Disposition"); // Expose headers if needed

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig); // Apply CORS configuration for all routes

        System.out.println("CORS configuration applied.");
        return source;
    }
}

//package com.yaduvanshi_brothers.api.config;
//
//import com.yaduvanshi_brothers.api.filter.JwtFilter;
//import com.yaduvanshi_brothers.api.service.CustomUserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//@Configuration
//@EnableWebSecurity
//public class SpringSecurity {
//
//    @Autowired
//    private CustomUserService customUserService;
//
//    @Autowired
//    private JwtFilter jwtFilter;
//
//    @Bean
//    protected SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        System.out.println("spring security starting ....");
//        return http.authorizeHttpRequests(request -> request
//                                .requestMatchers("/public/**").permitAll()
//                                .requestMatchers("/admin/**","/branch/**","/faculty/**","/student/**","/lectures/**","/send-mail/**","/image/**","/online-classes/**","/announcements/**","/one-time-mail/**","/assignments/**","/files/**").hasRole("ADMIN")
//                                .requestMatchers("/branch/**","/faculty/**","/student/**","/lectures/**","/send-mail/**","/image/**","/online-classes/**","/announcements/**","/one-time-mail/**","/assignments/**","/files/**").hasRole("FACULTY")
//                                .requestMatchers("/student/**","/lectures/**","/files/**").hasRole("STUDENT")
//
//                )
//                .csrf(AbstractHttpConfigurer::disable)
//                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
//                .build();
//
//    }
//
//
//
//    @Autowired
//    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(customUserService)
//                .passwordEncoder(passwordEncoder());
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        System.out.println("spring password generated ....");
//        System.out.println("password encoder "+ new BCryptPasswordEncoder());
//        return new BCryptPasswordEncoder();
//    }
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
//        return authenticationConfiguration.getAuthenticationManager();
//    }
//
//
//}
//
//

