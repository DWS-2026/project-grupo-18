package com.example.cybercert.Controllers.rest;
import com.example.cybercert.Models.User;
import com.example.cybercert.Services.UserService;
import com.example.cybercert.dto.UserDTO;
import com.example.cybercert.dto.UserMapper;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
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

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
            Optional<User> user = userService.findById(id);
            if (user.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(userMapper.toDTO(user.get()));
    }


    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users = userService.findAll();
        return ResponseEntity.ok(userMapper.toDTOs(users));
    }

    @PostMapping("/create")
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {
        User user = userMapper.toEntity(userDTO);
        User savedUser = userService.save(user);
        return ResponseEntity.ok(userMapper.toDTO(savedUser));
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
           
        Optional<User> user = userService.findById(id);

        if(user.isEmpty()){
            return ResponseEntity.notFound().build();
        }else{
            User existingUser = user.get();
            if (userDTO.username() != null) {
                existingUser.setUsername(userDTO.username());
            }
            if (userDTO.email() != null) {
                existingUser.setEmail(userDTO.email());
            }
            User updatedUser = userService.save(existingUser);
            return ResponseEntity.ok(userMapper.toDTO(updatedUser));
        }

    }

    @DeleteMapping("/delete/{id}")
    @Transactional
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        Optional<User> user = userService.findById(id);
        if(user.isEmpty()){
            return ResponseEntity.notFound().build();
        }else{
            userService.deleteByUsername(user.get().getUsername());
            return ResponseEntity.noContent().build();  
        }
    }

}
  