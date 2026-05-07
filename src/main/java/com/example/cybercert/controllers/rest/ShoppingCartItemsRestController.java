package com.example.cybercert.controllers.rest;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.example.cybercert.dto.CertificationDTO;
import com.example.cybercert.dto.CertificationMapper;
import com.example.cybercert.dto.ShoppingCartItemDTO;
import com.example.cybercert.dto.ShoppingCartItemMapper;

import com.example.cybercert.models.Certification;
import com.example.cybercert.models.ShoppingCartItem;
import com.example.cybercert.models.User;

import com.example.cybercert.services.CertificationService;
import com.example.cybercert.services.ShoppingCartService;
import com.example.cybercert.services.UserService;

import com.example.security.Role;
import com.example.security.jwt.JwtTokenProvider;

import jakarta.servlet.http.HttpServletRequest;
import io.jsonwebtoken.Claims;

@RestController
@RequestMapping("/api/v1/shopping-cart-items")
public class ShoppingCartItemsRestController {

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

        @Autowired
        private CertificationMapper certificationMapper;

        // =====================================================
        // GET MY CART ITEMS
        // =====================================================

        @GetMapping("/me")
        public ResponseEntity<List<ShoppingCartItemDTO>> showMyItems(
                        HttpServletRequest request) {

                User authenticatedUser = getAuthenticatedUser(request);

                List<ShoppingCartItem> items = shoppingCartService
                                .findByUserId(authenticatedUser.getId());

                return ResponseEntity.ok(
                                shoppingCartItemMapper.toDTOs(items));
        }

        // =====================================================
        // ADD ITEM TO SHOPPING CART
        // =====================================================

        @PostMapping("/{certificationId}")
        public ResponseEntity<Void> addCartItem(
                        @PathVariable Long certificationId,
                        HttpServletRequest request) {

                User authenticatedUser = getAuthenticatedUser(request);

                Certification certification = certificationService.findById(certificationId)
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.NOT_FOUND));

                if (shoppingCartService.hasPurchasedCertification(
                                authenticatedUser.getId(),
                                certificationId)) {

                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                }

                if (shoppingCartService.isCertificationInCart(
                                authenticatedUser.getId(),
                                certificationId)) {

                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                }

                shoppingCartService.addToCart(
                                authenticatedUser,
                                certification);

                URI location = ServletUriComponentsBuilder
                                .fromCurrentRequest()
                                .build()
                                .toUri();

                return ResponseEntity.created(location).build();
        }

        // =====================================================
        // DELETE ITEM FROM SHOPPING CART
        // =====================================================

        @DeleteMapping("/{certificationId}")
        public ResponseEntity<Void> deleteCartItem(
                        @PathVariable Long certificationId,
                        HttpServletRequest request) {

                User authenticatedUser = getAuthenticatedUser(request);

                if (!shoppingCartService.isCertificationInCart(
                                authenticatedUser.getId(),
                                certificationId)) {

                        return ResponseEntity.notFound().build();
                }

                shoppingCartService.removeFromCart(
                                authenticatedUser.getId(),
                                certificationId);

                return ResponseEntity.noContent().build();
        }
        // =====================================================
    // ADMIN GET USER CART
    // =====================================================

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ShoppingCartItemDTO>> showUserCart(
            @PathVariable Long userId,
            HttpServletRequest request) {

        User authenticatedUser = getAuthenticatedUser(request);

        if (authenticatedUser.getRole() != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<ShoppingCartItem> items =
                shoppingCartService.findByUserId(userId);

        return ResponseEntity.ok(
                shoppingCartItemMapper.toDTOs(items));
    }


        // =====================================================
        // AUTH HELPER
        // =====================================================

        private User getAuthenticatedUser(HttpServletRequest request) {

                Claims claims;

                try {

                        // Try cookies first
                        claims = jwtTokenProvider.validateToken(request, true);

                } catch (Exception e) {

                        try {

                                // Try Authorization header
                                claims = jwtTokenProvider.validateToken(request, false);

                        } catch (Exception e2) {

                                throw new ResponseStatusException(
                                                HttpStatus.UNAUTHORIZED);
                        }
                }

                String username = claims.getSubject();

                return userService.findByUsername(username)
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.UNAUTHORIZED));
        }
}