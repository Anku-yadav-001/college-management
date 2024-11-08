package com.yaduvanshi_brothers.api.filter;

import com.yaduvanshi_brothers.api.service.CustomUserService;
import com.yaduvanshi_brothers.api.utils.JwtUtility;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private CustomUserService customUserService;

    @Autowired
    private JwtUtility jwtUtility;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String jwt = getJwtFromCookies(request);
        String requestURI = request.getRequestURI();

        System.out.println("JWT Token found: " + jwt);
        System.out.println("Request URI: " + requestURI);

        // Allow OPTIONS requests (CORS preflight)
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        // Skip JWT token validation for public routes
        if (requestURI.startsWith("/public")) {
            filterChain.doFilter(request, response);
            return;
        }

        if (jwt != null) {
            String userName = jwtUtility.extractUsername(jwt);
            if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = customUserService.loadUserByUsername(userName);

                if (jwtUtility.validateToken(jwt)) {
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                } else {
                    // Token is invalid or expired
                    System.out.println("JWT validation failed.");
                    SecurityContextHolder.clearContext();
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT Token");
                    return;
                }
            }
        } else {
            System.out.println("No JWT token found.");
        }

        // Continue with the filter chain if token is valid or if accessing public resources
        filterChain.doFilter(request, response);
    }

    private String getJwtFromCookies(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                System.out.println("Checking Cookie Name: " + cookie.getName() + ", Value: " + cookie.getValue());
                if ("jwt".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        } else {
            System.out.println("No cookies found in the request.");
        }
        return null;
    }
}
