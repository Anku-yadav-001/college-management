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
    public ResponseEntity<?> healthCheckController() {
        System.out.println("health check api hit..ed");
        return ResponseEntity.status(HttpStatus.OK).body("server is working fine...");
    }

    @GetMapping("/check")
    public ResponseEntity<?> publicUrl() {
        System.out.println("check url hitted..");
        return ResponseEntity.status(HttpStatus.OK).body("this is public url");
    }

    @GetMapping("/get-all-users-on-this-website")
    public ResponseEntity<List<UserEntity>> userListController() {
        List<UserEntity> allUsers = userService.allUsersService();
        return new ResponseEntity<>(allUsers, HttpStatus.OK);
    }

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

            // Add cookies with appropriate SameSite and Secure attributes
            addSameSiteCookie(response, "jwt", jwt, 3600, true, isLocalhost);         // HttpOnly for security
            addSameSiteCookie(response, "username", userDetails.getUsername(), 3600, false, isLocalhost);  // Accessible from browser
            addSameSiteCookie(response, "role", role, 3600, false, isLocalhost);      // Accessible from browser

            return ResponseEntity.ok("Login successful");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect username or password");
        }
    }

    private void addSameSiteCookie(HttpServletResponse response, String name, String value, int maxAge, boolean httpOnly, boolean isLocalhost) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        cookie.setHttpOnly(httpOnly);

        // Construct the `Set-Cookie` header manually for additional attributes
        String setCookieHeader = cookie.getName() + "=" + cookie.getValue() + "; Path=" + cookie.getPath() + "; Max-Age=" + cookie.getMaxAge();

        if (httpOnly) {
            setCookieHeader += "; HttpOnly";
        }

        if (!isLocalhost) {
            // In production: `Secure` and `SameSite=None` for cross-origin access
            setCookieHeader += "; Secure; SameSite=None";
        } else {
            // In localhost: `SameSite=Lax`
            setCookieHeader += "; SameSite=Lax";
        }

        // Apply the header to response
        response.addHeader("Set-Cookie", setCookieHeader);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        String domain = "college-management-ebre.onrender.com"; // Production domain; update as needed

        // Clear JWT Cookie
        clearCookie(response, "jwt", domain);

        // Clear Username Cookie
        clearCookie(response, "username", domain);

        // Clear Role Cookie
        clearCookie(response, "role", domain);

        return ResponseEntity.ok("Logout successful...");
    }

    private void clearCookie(HttpServletResponse response, String name, String domain) {
        Cookie cookie = new Cookie(name, null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(false);
        cookie.setDomain(domain);

        // Construct `Set-Cookie` header with Secure and SameSite attributes to match login settings
        response.addHeader("Set-Cookie", name + "=; Path=/; Max-Age=0; Domain=" + domain + "; Secure; SameSite=None");
    }

    @PostMapping("/add-user")
    public ResponseEntity<String> userUserByAdminController(@RequestBody UserEntity userEntity) {
        userService.adduserByAdminService(userEntity);
        return new ResponseEntity<>("User added successfully", HttpStatus.CREATED);
    }

}
