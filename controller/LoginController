package edu.usc.csci310.project.controller;

import edu.usc.csci310.project.service.UserService;
import edu.usc.csci310.project.service.LoginAttemptService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@RestController
public class LoginController {

    private final LoginAttemptService loginAttemptService;
    private final UserService userService;
    private final SecretKey jwtSecretKey;


    public LoginController(LoginAttemptService loginAttemptService, UserService userService, SecretKey jwtSecretKey) {
        this.loginAttemptService = loginAttemptService;
        this.userService = userService;
        this.jwtSecretKey = jwtSecretKey;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password, HttpServletResponse response) {
        if (!loginAttemptService.canAttemptLogin(username)) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "You are locked out. Please wait " + loginAttemptService.getLockoutTime(username) + " seconds."));
        }
        boolean isAuthenticated = userService.authenticate(username, password);

        if (isAuthenticated) {
            loginAttemptService.loginSucceeded(username);
            Optional<Long> userId = userService.findUserIdByUsername(username);
            if (userId.isEmpty()) {
                return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Login failed"));
            }

            String token = generateJwtToken(username, userId.get());
            Cookie jwtCookie = new Cookie("JWT", token);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(60*60*24);
            response.addCookie(jwtCookie);
            return ResponseEntity.ok(Collections.singletonMap("error", ""));
        } else {
            loginAttemptService.loginFailed(username);
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Login failed"));
        }
    }

    public String generateJwtToken(String username, Long id) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        //10 hours
//        long jwtExpirationInMillis = 36000000;
//        Date exp = new Date(nowMillis + jwtExpirationInMillis);

        return Jwts.builder()
                .claim("username", username)
                .claim("id", id)
                .issuedAt(now)
//                .expiration(exp)
                .signWith(jwtSecretKey)
                .compact();
    }

    @GetMapping("/api/auth/validate")
    public ResponseEntity<?> validateSession(Authentication authentication, HttpServletResponse response) {
        // Check if authentication object is null or if the authentication name is null
        if (authentication == null || authentication.getName() == null) {
            // Create and add the cookie to signify an unauthorized request
            Cookie cookie = new Cookie("JWT", null);
            cookie.setPath("/");
            cookie.setMaxAge(0);
            cookie.setHttpOnly(true);
            response.addCookie(cookie);
            // Return an unauthorized response
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("error", "Unauthorized"));
        } else {
            // Proceed with checking the user ID based on the authentication name
            Optional<Long> uid = userService.findUserIdByUsername(authentication.getName());
            if (uid.isEmpty()) {
                // If user ID not found, treat as unauthorized
                Cookie cookie = new Cookie("JWT", null);
                cookie.setPath("/");
                cookie.setMaxAge(0);
                cookie.setHttpOnly(true);
                response.addCookie(cookie);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("error", "Unauthorized"));
            } else {
                // If user ID found, proceed as authorized
                return ResponseEntity.ok(Collections.singletonMap("username", authentication.getName()));
            }
        }
    }


    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response, Authentication authentication) {
        Cookie cookie = new Cookie("JWT", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return ResponseEntity.ok(Collections.singletonMap("error", ""));
    }

}
