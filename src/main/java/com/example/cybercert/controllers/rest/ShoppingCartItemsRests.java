package com.example.cybercert.controllers.rest;

import com.example.cybercert.dto.ShoppingCartItemMapper;
import com.example.cybercert.models.ShoppingCartItem;
import com.example.cybercert.services.CertificationService;
import com.example.cybercert.services.ShoppingCartService;
import com.example.cybercert.services.UserService;
import com.example.cybercert.dto.ShoppingCartItemDTO;
import com.example.cybercert.models.Certification;
import com.example.cybercert.models.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.servlet.http.HttpServletRequest;
import io.jsonwebtoken.Claims;
import com.example.security.jwt.JwtTokenProvider;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/v1/shopping-cart-items")
public class ShoppingCartItemsRests {

    @Autowired
    private UserService userService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private CertificationService certificationService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ShoppingCartItemMapper shoppingCartItemMapper;

    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Cart items retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/me")
    public ResponseEntity<List<Certification>> showMyItems(HttpServletRequest request) {
        try {
            // Get JWT token (try cookies first, then headers)
            Claims claims;
            try {
                claims = jwtTokenProvider.validateToken(request, true);
            } catch (Exception e) {
                try {
                    claims = jwtTokenProvider.validateToken(request, false);
                } catch (Exception e2) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                }
            }

            String username = claims.getSubject();
            User authenticatedUser = userService.findByUsername(username).orElse(null);
            if (authenticatedUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            List<Certification> cartCertifications = shoppingCartService.getCartCertifications(authenticatedUser.getId());

            return ResponseEntity.ok().body(cartCertifications);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCartItem(@PathVariable Long id, HttpServletRequest request) {
        try {
            // Get JWT token (try cookies first, then headers)
            Claims claims;
            try {
                claims = jwtTokenProvider.validateToken(request, true);
            } catch (Exception e) {
                try {
                    claims = jwtTokenProvider.validateToken(request, false);
                } catch (Exception e2) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                }
            }

            String username = claims.getSubject();
            User authenticatedUser = userService.findByUsername(username).orElse(null);
            if (authenticatedUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            ShoppingCartItem item = shoppingCartService.findById(id).orElse(null);
            if (item == null || !item.getUser().getId().equals(authenticatedUser.getId())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            shoppingCartService.removeFromCart(authenticatedUser.getId(), id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{certificationId}")
    public ResponseEntity<Void> addCartItem(@PathVariable Long certificationId, HttpServletRequest request) {
        try {
            // Get JWT token (try cookies first, then headers)
            Claims claims;
            try {
                claims = jwtTokenProvider.validateToken(request, true);
            } catch (Exception e) {
                try {
                    claims = jwtTokenProvider.validateToken(request, false);
                } catch (Exception e2) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                }
            }

            String username = claims.getSubject();
            User authenticatedUser = userService.findByUsername(username).orElse(null);
            if (authenticatedUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            shoppingCartService.addToCart(authenticatedUser, certificationService.findById(certificationId).orElse(null));
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShoppingCartItemDTO> getCartItemById(@PathVariable Long id, HttpServletRequest request) {
        try {
            Claims claims;
            try {
                claims = jwtTokenProvider.validateToken(request, true);
            } catch (Exception e) {
                try {
                    claims = jwtTokenProvider.validateToken(request, false);
                } catch (Exception e2) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                }
            }

            String username = claims.getSubject();
            User authenticatedUser = userService.findByUsername(username).orElse(null);
            if (authenticatedUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            ShoppingCartItem item = shoppingCartService.findById(id).orElse(null);
            if (item == null || !item.getUser().getId().equals(authenticatedUser.getId())) {
                return ResponseEntity.notFound().build();
            }

            ShoppingCartItemDTO dto = shoppingCartItemMapper.toDTO(item);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

