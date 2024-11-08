package com.yaduvanshi_brothers.api.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.CrossOrigin;
import com.yaduvanshi_brothers.api.entity.FacultyEntity;
import com.yaduvanshi_brothers.api.entity.UserEntity;
import com.yaduvanshi_brothers.api.service.CustomUserService;
import com.yaduvanshi_brothers.api.service.FacultyService;
import com.yaduvanshi_brothers.api.service.ImageService;
import com.yaduvanshi_brothers.api.service.UserService;
import com.yaduvanshi_brothers.api.utils.JwtUtility;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/public")
@Slf4j
public class PublicController {

    @Autowired
    private UserService userService;

    @Autowired
    private CustomUserService customUserService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtility jwtUtility;

    @Autowired
    private ImageService imageService;

    @Autowired
    private FacultyService facultyService;

    @GetMapping("/health-check")
    public ResponseEntity<?> healthCheckController(){
        System.out.println("health check api hit..ed");
        return  ResponseEntity.status(HttpStatus.OK).body("server is working fine...");
    }

    @GetMapping("/check")
    public ResponseEntity<?> publicUrl(){
        System.out.println("check url hitted..");
        return  ResponseEntity.status(HttpStatus.OK).body("this is public url");
    }

    @GetMapping("/get-all-users-on-this-website")
    public ResponseEntity<List<UserEntity>> userListController(){
        List<UserEntity> allUsers = userService.allUsersService();
        return new ResponseEntity<>(allUsers, HttpStatus.OK);
    }
    // Adjusted login method
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserEntity userData, HttpServletRequest request, HttpServletResponse response) {
        try {
            // Authenticate the user
            System.out.println("In login controller..." + userData.getUsername() + " " + userData.getPassword() + " " + userData.getRoles());
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userData.getUsername(), userData.getPassword())
            );

            UserDetails userDetails = customUserService.loadUserByUsername(userData.getUsername());

            // Generate JWT token
            String jwt = jwtUtility.generateToken(userDetails.getUsername());

            // Extract role from authorities
            String role = userDetails.getAuthorities().stream()
                    .findFirst().orElseThrow().getAuthority();

            // Check if running on localhost
            boolean isLocalhost = "localhost".equals(request.getServerName());
            System.out.println("Server name: " + isLocalhost + " " + request.getServerName());

            // Helper method to add cookie with conditional SameSite attribute
            addSameSiteCookie(response, "jwt", jwt, 3600, true, isLocalhost);
            addSameSiteCookie(response, "username", userDetails.getUsername(), 3600, false, isLocalhost);
            addSameSiteCookie(response, "role", role, 3600, false, isLocalhost);

            return ResponseEntity.ok("Login successful");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect username or password");
        }
    }

    // Helper method to add cookies with conditional SameSite
    private void addSameSiteCookie(HttpServletResponse response, String name, String value, int maxAge, boolean httpOnly, boolean isLocalhost) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        cookie.setHttpOnly(httpOnly);
        cookie.setSecure(!isLocalhost); // Enable Secure for non-localhost only

        // Manually construct Set-Cookie header with SameSite attribute
        StringBuilder cookieHeader = new StringBuilder(name + "=" + value + "; Path=/; Max-Age=" + maxAge);
        if (httpOnly) cookieHeader.append("; HttpOnly");
        if (!isLocalhost) cookieHeader.append("; Secure; SameSite=None");
        else cookieHeader.append("; SameSite=Lax");

        response.addHeader("Set-Cookie", cookieHeader.toString());
    }

// Logout method remains unchanged except for cookie clearing

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        // Clear JWT cookie with SameSite attribute manually
        Cookie jwtCookie = new Cookie("jwt", null);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0);
        jwtCookie.setHttpOnly(false);
        jwtCookie.setDomain("college-management-ebre.onrender.com"); // Set backend domain
        response.addCookie(jwtCookie);
        response.setHeader("Set-Cookie", "jwt=; HttpOnly; Secure; SameSite=None; Path=/; Max-Age=0; Domain=college-management-ebre.onrender.com");

        // Clear Username Cookie with SameSite attribute manually
        Cookie usernameCookie = new Cookie("username", null);
        usernameCookie.setPath("/");
        usernameCookie.setMaxAge(0);
        usernameCookie.setHttpOnly(false);
        usernameCookie.setDomain("college-management-ebre.onrender.com"); // Set backend domain
        response.addCookie(usernameCookie);
        response.setHeader("Set-Cookie", "username=; Secure; SameSite=None; Path=/; Max-Age=0; Domain=college-management-ebre.onrender.com");

        // Clear Role Cookie with SameSite attribute manually
        Cookie roleCookie = new Cookie("role", null);
        roleCookie.setPath("/");
        roleCookie.setMaxAge(0);
        roleCookie.setHttpOnly(false);
        roleCookie.setDomain("college-management-ebre.onrender.com"); // Set backend domain
        response.addCookie(roleCookie);
        response.setHeader("Set-Cookie", "role=; Secure; SameSite=None; Path=/; Max-Age=0; Domain=college-management-ebre.onrender.com");

        return ResponseEntity.ok("Logout successful...");
    }


    @PostMapping("/add-user")
    public ResponseEntity<String> userUserByAdminController(@RequestBody UserEntity userEntity){
        userService.adduserByAdminService(userEntity);
        return new ResponseEntity<>("User added successfully", HttpStatus.CREATED);
    }

}
