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
    public ResponseEntity<?> publicUrl(){
        System.out.println("check url hitted..");
        return  ResponseEntity.status(HttpStatus.OK).body("this is public url");
    }

    @GetMapping("/get-all-users-on-this-website")
    public ResponseEntity<List<UserEntity>> userListController(){
        List<UserEntity> allUsers = userService.allUsersService();
        return new ResponseEntity<>(allUsers, HttpStatus.OK);
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserEntity userData, HttpServletResponse response) {
        try {
            // Authenticate the user
            System.out.println("in login controller..."+userData.getUsername()+" "+userData.getPassword()+" "+userData.getRoles());
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userData.getUsername(), userData.getPassword())
            );

            UserDetails userDetails = customUserService.loadUserByUsername(userData.getUsername());

            // Generate JWT token
            String jwt = jwtUtility.generateToken(userDetails.getUsername());

            // Extract role from authorities
            String role = userDetails.getAuthorities().stream()
                    .findFirst().orElseThrow().getAuthority();

            // Set JWT cookie with SameSite attribute manually
            Cookie jwtCookie = new Cookie("jwt", jwt);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setSecure(true); // Ensure it is sent over HTTPS
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(3600);
            jwtCookie.setDomain("college-management-ebre.onrender.com"); // Set backend domain
            response.addCookie(jwtCookie);
            response.setHeader("Set-Cookie", "jwt=" + jwt + "; HttpOnly; Secure; SameSite=None; Path=/; Max-Age=3600; Domain=college-management-ebre.onrender.com");


            // Set username cookie with SameSite attribute manually
            Cookie usernameCookie = new Cookie("username", userDetails.getUsername());
            usernameCookie.setSecure(true);
            usernameCookie.setHttpOnly(false); // Allow client to access
            usernameCookie.setPath("/");
            usernameCookie.setMaxAge(3600);
            usernameCookie.setDomain("college-management-ebre.onrender.com"); // Set backend domain
            response.addCookie(usernameCookie);
            response.setHeader("Set-Cookie", "username=" + userDetails.getUsername() + "; Secure; SameSite=None; Path=/; Max-Age=3600; Domain=college-management-ebre.onrender.com");

            // Set role cookie with SameSite attribute manually
            Cookie roleCookie = new Cookie("role", role);
            roleCookie.setSecure(true);
            roleCookie.setHttpOnly(false); // Allow client to access
            roleCookie.setPath("/");
            roleCookie.setMaxAge(3600);
            roleCookie.setDomain("college-management-ebre.onrender.com"); // Set backend domain
            response.addCookie(roleCookie);
            response.setHeader("Set-Cookie", "role=" + role + "; Secure; SameSite=None; Path=/; Max-Age=3600; Domain=college-management-ebre.onrender.com");

            return ResponseEntity.ok("Login successful");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect username or password");
        }
    }

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
