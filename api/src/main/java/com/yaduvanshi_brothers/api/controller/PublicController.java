package com.yaduvanshi_brothers.api.controller;

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
        return  ResponseEntity.status(HttpStatus.OK).body("server is working fine");
    }

    @GetMapping
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
        System.out.println("user login cred from ui -- "+userData+"  "+ response);
        try {
            // Authenticate the user
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userData.getUsername(), userData.getPassword())
            );

            UserDetails userDetails = customUserService.loadUserByUsername(userData.getUsername());

            String jwt = jwtUtility.generateToken(userDetails.getUsername());

            String role = userDetails.getAuthorities().stream()
                    .findFirst().orElseThrow().getAuthority();
            Cookie jwtCookie = new Cookie("jwt", jwt);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setSecure(false);
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(3600);
            response.addCookie(jwtCookie);
            System.out.println("cookies setted - -" + jwtCookie);

            Cookie usernameCookie = new Cookie("username", userDetails.getUsername());
            usernameCookie.setSecure(false);
            usernameCookie.setHttpOnly(false);
            usernameCookie.setPath("/");
            usernameCookie.setMaxAge(3600);
            response.addCookie(usernameCookie);

            Cookie roleCookie = new Cookie("role", role);
            roleCookie.setSecure(false); // Set true if using HTTPS
            roleCookie.setHttpOnly(false); // Role cookie can be accessed client-side
            roleCookie.setPath("/");// allow for all paths
            roleCookie.setMaxAge(3600); // 1 day expiration (24 hours)
            response.addCookie(roleCookie);

            return ResponseEntity.ok("Login successful");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect username or password");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        Cookie jwtCookie = new Cookie("jwt", null);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0);
        jwtCookie.setHttpOnly(false);
        response.addCookie(jwtCookie);

        // Clear Username Cookie
        Cookie usernameCookie = new Cookie("username", null);
        usernameCookie.setPath("/");
        usernameCookie.setMaxAge(0);
        usernameCookie.setHttpOnly(false);
        response.addCookie(usernameCookie);

        // Clear Role Cookie
        Cookie roleCookie = new Cookie("role", null);
        roleCookie.setPath("/");
        roleCookie.setMaxAge(0);
        roleCookie.setHttpOnly(false);
        response.addCookie(roleCookie);

        return ResponseEntity.ok("Logout successful...");
    }



    @PostMapping("/add-user")
    public ResponseEntity<String> userUserByAdminController(@RequestBody UserEntity userEntity){
        userService.adduserByAdminService(userEntity);
        return new ResponseEntity<>("User added successfully", HttpStatus.CREATED);
    }

}