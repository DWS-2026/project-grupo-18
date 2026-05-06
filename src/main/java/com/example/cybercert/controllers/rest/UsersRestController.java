package com.example.cybercert.controllers.rest;
import com.example.cybercert.dto.FullUserDTO;
import com.example.cybercert.dto.UserDTO;
import com.example.cybercert.dto.UserMapper;
import com.example.cybercert.models.Image;
import com.example.cybercert.models.User;
import com.example.cybercert.services.ImageService;
import com.example.cybercert.services.UserService;

import java.io.IOException;
import java.util.Optional;

import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.example.security.jwt.JwtTokenProvider;
import com.example.security.Role;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "REST operations on users")
public class UsersRestController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final ImageService imageService;
    private final JwtTokenProvider jwtTokenProvider;
        private final PasswordEncoder passwordEncoder;

    public UsersRestController(UserService userService,
            UserMapper userMapper,
            ImageService imageService,
                        JwtTokenProvider jwtTokenProvider,
                        PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.imageService = imageService;
        this.jwtTokenProvider = jwtTokenProvider;
                this.passwordEncoder = passwordEncoder;
    }

    @Operation(summary = "Get a user by id", 
            description = "Returns the information for a specific user, including the ID of their profile image (requires ADMIN role)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found", 
                    content = @Content(schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden - ADMIN role required")
    })
    @GetMapping("/{id}")
        public ResponseEntity<UserDTO> getUserById(@Parameter(description = "User identifier") @PathVariable Long id) {
            Optional<User> user = userService.findById(id);
            if (user.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(userMapper.toDTO(user.get()));
    }


        @Operation(summary = "Get all users")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User list returned successfully")
    })
    @GetMapping
    public ResponseEntity<Page<UserDTO>> getAllUsers(Pageable pageable) {
        Page<User> users = userService.findAll(pageable);
        Page<UserDTO> dtoPage = users.map(userMapper::toDTO);
        return ResponseEntity.ok(dtoPage);
    }

    @Operation(summary = "Create a user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User created successfully")
    })
    @PostMapping("/register")
    public ResponseEntity<Void> createUser(@RequestBody FullUserDTO fullUserDTO) {
                if (userService.findByUsername(fullUserDTO.username()).isPresent()) {
                        return ResponseEntity.badRequest().build();
                }

                if (userService.findByEmail(fullUserDTO.email()).isPresent()) {
            return ResponseEntity.badRequest().build();
        }

        User user = userMapper.toEntity(fullUserDTO);
                user.setId(null);
                user.setRole(Role.USER);
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                userService.save(user);
                return ResponseEntity.ok().build();
    }

    @Operation(summary = "Partially update a user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PatchMapping("/update/{id}")
        public ResponseEntity<FullUserDTO> updateUser(@Parameter(description = "User identifier") @PathVariable Long id, @RequestBody FullUserDTO fullUserDTO) {
           
        Optional<User> user = userService.findById(id);

        if(user.isEmpty()){
            return ResponseEntity.notFound().build();
        }else{
            User existingUser = user.get();
            if (fullUserDTO.username() != null) {
                existingUser.setUsername(fullUserDTO.username());
            }
            if (fullUserDTO.email() != null) {
                existingUser.setEmail(fullUserDTO.email());
            }
            User updatedUser = userService.save(existingUser);
            return ResponseEntity.ok(userMapper.toFullDTO(updatedUser));
        }

    }

    @Operation(summary = "Delete a user")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/delete/{id}")
    @Transactional
        public ResponseEntity<Void> deleteUser(@Parameter(description = "User identifier") @PathVariable Long id) {
        Optional<User> user = userService.findById(id);
        if(user.isEmpty()){
            return ResponseEntity.notFound().build();
        }else{
            userService.deleteByUsername(user.get().getUsername());
            return ResponseEntity.noContent().build();  
        }
    }

    @GetMapping("/me")
    @Operation(summary = "Get the authenticated user's profile", 
            description = "Returns the information for the currently authenticated user, including the ID of their profile image")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User profile returned successfully",
                    content = @Content(schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized or expired token")
    })
    public ResponseEntity<UserDTO> getMe(HttpServletRequest request) {
            try {
                    Claims claims = jwtTokenProvider.validateToken(request, true);
                    String username = claims.getSubject();
                    
                    User user = userService.findByUsername(username).orElse(null);
                    if (user == null) {
                            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                    }
                    
                    return ResponseEntity.ok(userMapper.toDTO(user));
            } catch (Exception ex) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
    }

    @PostMapping(value = "/me/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload or update profile image", 
            description = "Allows the authenticated user to upload or update their profile image. Supported formats: JPEG, PNG, WebP. Maximum size: 10MB")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Image uploaded successfully, the updated profile is returned",
                    content = @Content(schema = @Schema(implementation = UserDTO.class))),
            @ApiResponse(responseCode = "400", description = "Empty file, unsupported format, or file too large"),
            @ApiResponse(responseCode = "401", description = "Unauthorized or expired token")
    })
    public ResponseEntity<UserDTO> uploadProfileImage(HttpServletRequest request,
                    @org.springframework.web.bind.annotation.RequestParam("imageFile") org.springframework.web.multipart.MultipartFile imageFile) throws IOException {

            try {
                    Claims claims = jwtTokenProvider.validateToken(request, true);
                    String username = claims.getSubject();
                    
                    User user = userService.findByUsername(username).orElse(null);
                    if (user == null) {
                            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                    }

                    if (imageFile.isEmpty()) {
                            return ResponseEntity.badRequest().build();
                    }

                    String contentType = imageFile.getContentType();
                    if (contentType == null ||
                                    !(contentType.equals("image/jpeg") ||
                                                    contentType.equals("image/png") ||
                                                    contentType.equals("image/webp"))) {
                            return ResponseEntity.badRequest().build();
                    }

                    if (imageFile.getSize() > 10 * 1024 * 1024) {
                            return ResponseEntity.badRequest().build();
                    }

                    Image profileImage;
                    if (user.getProfileImage() != null) {
                            profileImage = imageService.replaceImageFile(user.getProfileImage().getId(), imageFile.getInputStream());
                    } else {
                            profileImage = imageService.createImage(imageFile.getInputStream());
                    }

                    user.setProfileImage(profileImage);
                    User updatedUser = userService.save(user);

                    return ResponseEntity.ok(userMapper.toDTO(updatedUser));
            } catch (Exception ex) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
    }

}
  