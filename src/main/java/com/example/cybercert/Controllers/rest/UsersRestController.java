package com.example.cybercert.Controllers.rest;
import com.example.cybercert.Services.UserService;
import com.example.cybercert.dto.UserDTO;
import com.example.cybercert.dto.UserMapper;

import java.security.Principal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UsersRestController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UsersRestController(UserService userService,
            UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }

        return userService.findByUsername(principal.getName())
                .map(userMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id, Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }

        var authenticatedUser = userService.findByUsername(principal.getName());
        
        if (authenticatedUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        var user = authenticatedUser.get();
        
        if (user.getRole() == com.example.cybercert.Role.ADMIN) {
            return userService.findById(id)
                    .map(userMapper::toDTO)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }else{
            return ResponseEntity.status(403).build();
        }
    }

}
