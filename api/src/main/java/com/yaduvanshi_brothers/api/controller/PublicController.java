package com.yaduvanshi_brothers.api.controller;

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
        return  ResponseEntity.status(HttpStatus.OK).body("server is working fine");
    }

    @GetMapping("/check")
    public ResponseEntity<?> publicurl(){
        return  ResponseEntity.status(HttpStatus.OK).body("this is public url");
    }

    @GetMapping("/get-all-users-on-this-website")
    public ResponseEntity<List<UserEntity>> userListController(){
        List<UserEntity> allUsers = userService.allUsersService();
        return new ResponseEntity<>(allUsers, HttpStatus.OK);
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserEntity userData, HttpServletResponse response) {
        System.out.println("user login cred from ui -- " + userData + "  " + response);
        try {
            // Authenticate the user
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userData.getUsername(), userData.getPassword())
            );

            UserDetails userDetails = customUserService.loadUserByUsername(userData.getUsername());

            String jwt = jwtUtility.generateToken(userDetails.getUsername());
            String role = userDetails.getAuthorities().stream()
                    .findFirst().orElseThrow().getAuthority();

            // Create JWT Cookie
            Cookie jwtCookie = new Cookie("jwt", jwt);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setSecure(true);  // Use secure cookie if on HTTPS
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(3600);  // 1 hour expiration
            jwtCookie.setDomain("college-management-brrb.onrender.com");
            // Manually set SameSite=None since it's not available in the Cookie class directly
            response.addHeader("Set-Cookie", String.format(
                    "%s=%s; Path=%s; Domain=%s; Max-Age=%d; HttpOnly; Secure; SameSite=None",
                    jwtCookie.getName(), jwtCookie.getValue(), jwtCookie.getPath(),
                    jwtCookie.getDomain(), jwtCookie.getMaxAge()
            ));

            // Create Username Cookie
            Cookie usernameCookie = new Cookie("username", userDetails.getUsername());
            usernameCookie.setSecure(true);
            usernameCookie.setHttpOnly(false); // Accessible client-side
            usernameCookie.setPath("/");
            usernameCookie.setMaxAge(3600);
            usernameCookie.setDomain("college-management-brrb.onrender.com");
            // Manually set SameSite=None
            response.addHeader("Set-Cookie", String.format(
                    "%s=%s; Path=%s; Domain=%s; Max-Age=%d; HttpOnly=false; Secure; SameSite=None",
                    usernameCookie.getName(), usernameCookie.getValue(), usernameCookie.getPath(),
                    usernameCookie.getDomain(), usernameCookie.getMaxAge()
            ));

            // Create Role Cookie
            Cookie roleCookie = new Cookie("role", role);
            roleCookie.setSecure(true);
            roleCookie.setHttpOnly(false); // Accessible client-side
            roleCookie.setPath("/");
            roleCookie.setMaxAge(3600);
            roleCookie.setDomain("college-management-brrb.onrender.com");
            // Manually set SameSite=None
            response.addHeader("Set-Cookie", String.format(
                    "%s=%s; Path=%s; Domain=%s; Max-Age=%d; HttpOnly=false; Secure; SameSite=None",
                    roleCookie.getName(), roleCookie.getValue(), roleCookie.getPath(),
                    roleCookie.getDomain(), roleCookie.getMaxAge()
            ));

            return ResponseEntity.ok("Login successful");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect username or password");
        }
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        // Clear JWT Cookie
        Cookie jwtCookie = new Cookie("jwt", null);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0);  // Set to 0 to expire the cookie
        jwtCookie.setHttpOnly(false); // Don't need HttpOnly for logout
        response.addCookie(jwtCookie);

        // Clear Username Cookie
        Cookie usernameCookie = new Cookie("username", null);
        usernameCookie.setPath("/");
        usernameCookie.setMaxAge(0);
        usernameCookie.setHttpOnly(false); // Don't need HttpOnly for logout
        response.addCookie(usernameCookie);

        // Clear Role Cookie
        Cookie roleCookie = new Cookie("role", null);
        roleCookie.setPath("/");
        roleCookie.setMaxAge(0);
        roleCookie.setHttpOnly(false); // Don't need HttpOnly for logout
        response.addCookie(roleCookie);

        return ResponseEntity.ok("Logout successful...");
    }




    @PostMapping("/add-user")
    public ResponseEntity<String> userUserByAdminController(@RequestBody UserEntity userEntity){
        userService.adduserByAdminService(userEntity);
        return new ResponseEntity<>("User added successfully", HttpStatus.CREATED);
    }

}
