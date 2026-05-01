package com.example.cybercert.Controllers.rest;
import com.example.cybercert.Models.User;
import com.example.cybercert.Services.UserService;
import com.example.cybercert.dto.FullUserDTO;
import com.example.cybercert.dto.UserDTO;
import com.example.cybercert.dto.UserMapper;

import java.util.List;
import java.util.Optional;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "Operaciones REST sobre usuarios")
public class UsersRestController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UsersRestController(UserService userService,
            UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @Operation(summary = "Obtener un usuario por id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@Parameter(description = "Identificador del usuario") @PathVariable Long id) {
            Optional<User> user = userService.findById(id);
            if (user.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(userMapper.toDTO(user.get()));
    }


    @Operation(summary = "Obtener todos los usuarios")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado de usuarios devuelto correctamente")
    })
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users = userService.findAll();
        return ResponseEntity.ok(userMapper.toDTOs(users));
    }

    @Operation(summary = "Crear un usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario creado correctamente", content = @Content(schema = @Schema(implementation = FullUserDTO.class)))
    })
    @PostMapping("/create")
    public ResponseEntity<FullUserDTO> createUser(@RequestBody FullUserDTO fullUserDTO) {
        User user = userMapper.toEntity(fullUserDTO);
        User savedUser = userService.save(user);
        return ResponseEntity.ok(userMapper.toFullDTO(savedUser));
    }

    @Operation(summary = "Actualizar parcialmente un usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @PatchMapping("/update/{id}")
    public ResponseEntity<FullUserDTO> updateUser(@Parameter(description = "Identificador del usuario") @PathVariable Long id, @RequestBody FullUserDTO fullUserDTO) {
           
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

    @Operation(summary = "Eliminar un usuario")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Usuario eliminado correctamente"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @DeleteMapping("/delete/{id}")
    @Transactional
    public ResponseEntity<Void> deleteUser(@Parameter(description = "Identificador del usuario") @PathVariable Long id) {
        Optional<User> user = userService.findById(id);
        if(user.isEmpty()){
            return ResponseEntity.notFound().build();
        }else{
            userService.deleteByUsername(user.get().getUsername());
            return ResponseEntity.noContent().build();  
        }
    }

}
  