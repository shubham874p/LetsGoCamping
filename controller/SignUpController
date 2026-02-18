package edu.usc.csci310.project.controller;

import edu.usc.csci310.project.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
public class SignUpController {
    private final UserService userService;

    public SignUpController(UserService userService){
        this.userService=userService;
    }
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestParam String username, @RequestParam String password) {
        if (userService.userExists(username)){
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Sign up failed user exists"));
        }
        userService.createUser(username, password);
        return ResponseEntity.ok(Collections.singletonMap("error","Sign up succeeded"));
    }
}
