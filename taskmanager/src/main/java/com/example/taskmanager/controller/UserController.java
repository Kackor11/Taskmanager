package com.example.taskmanager.controller;

import com.example.taskmanager.model.User;
import com.example.taskmanager.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        String zitadelId = jwt.getSubject();

        String tempUsername = jwt.getClaimAsString("preferred_username");
        final String finalUsername = (tempUsername != null) ? tempUsername : jwt.getClaimAsString("email");

        User user = userRepository.findByZitadelId(zitadelId).orElseGet(() -> {
            User newUser = new User();
            newUser.setZitadelId(zitadelId);
            newUser.setUsername(finalUsername != null ? finalUsername : "Unknown_User");
            return userRepository.save(newUser);
        });

        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "zitadelId", user.getZitadelId(),
                "username", user.getUsername()
        ));
    }
}