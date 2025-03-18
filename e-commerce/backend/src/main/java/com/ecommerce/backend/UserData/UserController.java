package com.ecommerce.backend.UserData;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import com.ecommerce.backend.JWTData.JwtHelper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser( @Valid  @RequestBody UserEntity user) {

        if (userService.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body("Email is already taken!");
        }

        userService.createUser(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());

        // Generate JWT token
        String token = jwtHelper.generateToken(userDetails);

        return ResponseEntity.ok(token);
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserEntity> getUserByEmail(@PathVariable String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        UserEntity user = (UserEntity) userDetails;
        return ResponseEntity.ok(user);
    }
}
