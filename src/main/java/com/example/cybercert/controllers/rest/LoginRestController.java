package com.example.cybercert.controllers.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.security.jwt.AuthResponse;
import com.example.security.jwt.AuthResponse.Status;
import com.example.security.jwt.LoginRequest;
import com.example.security.jwt.UserLoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Authentication endpoints")
public class LoginRestController {

        @Autowired
        private UserLoginService userService;

        @PostMapping("/login")
        @Operation(summary = "User login", description = "Authenticates a user and returns JWT tokens in cookies")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Login successful, tokens created in cookies",
                        content = @Content(schema = @Schema(implementation = AuthResponse.class))),
                @ApiResponse(responseCode = "401", description = "Invalid credentials")
        })
        public ResponseEntity<AuthResponse> login(
                        @RequestBody LoginRequest loginRequest,
                        HttpServletResponse response) {

                return userService.login(response, loginRequest);
        }

        @PostMapping("/refresh")
        @Operation(summary = "Refresh token", description = "Obtains a new access token using the refresh token")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
                @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token")
        })
        public ResponseEntity<AuthResponse> refreshToken(
                        @CookieValue(name = "RefreshToken", required = false) String refreshToken, HttpServletResponse response) {

                return userService.refresh(response, refreshToken);
        }



        @PostMapping("/logout")
        @Operation(summary = "Log out", description = "Invalidates the user's JWT tokens")
        @ApiResponses({
                @ApiResponse(responseCode = "200", description = "Logged out successfully")
        })
        public ResponseEntity<AuthResponse> logOut(HttpServletResponse response) {
                return ResponseEntity.ok(new AuthResponse(Status.SUCCESS, userService.logout(response)));
        }
}


