package com.example.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;

import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.security.jwt.JwtTokenProvider;
import com.example.security.jwt.JwtRequestFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Autowired
    private RepositoryUserDetailsService userDetailsService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);

        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http.authenticationProvider(authenticationProvider());

        http.securityMatcher("/api/**");

        http
                .authorizeHttpRequests(authorize -> authorize
                        // AUTH API
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/login", "/api/v1/auth/refresh",
                                "/api/v1/auth/logout")
                        .permitAll()

                        // USERS API
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/me").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/users/me/profile-image").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/users/register").permitAll()
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/users/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/users/**").hasRole("ADMIN")

                        // CERTIFICATIONS API
                        .requestMatchers(HttpMethod.GET, "/api/v1/certifications/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/certifications/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/certifications/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/certifications/**").hasRole("ADMIN")

                        // COMMENTS API
                        .requestMatchers(HttpMethod.GET, "/api/v1/comments/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/comments").hasRole("USER")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/comments/**").hasRole("USER")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/comments/**").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/images/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/v1/images/**").hasRole("ADMIN")

                        // SHOPPING CART API
                        .requestMatchers(HttpMethod.GET, "/api/v1/shopping-cart-items/me").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/shopping-cart-items/me/certifications")
                        .authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/v1/shopping-cart-items/{id}").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/shopping-cart-items/**").hasRole("USER")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/shopping-cart-items/**").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/shopping-cart-items/user/**").hasRole("ADMIN")

                        .anyRequest().authenticated());

        // Disable Form login Authentication
        http.formLogin(formLogin -> formLogin.disable());

        // Disable CSRF protection (it is difficult to implement in REST APIs)
        http.csrf(csrf -> csrf.disable());

        // Disable Basic Authentication
        http.httpBasic(httpBasic -> httpBasic.disable());

        // Allow stateful sessions for API (inherit from web session)
        http.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));

        // Add JWT Token filter
        http.addFilterBefore(new JwtRequestFilter(userDetailsService, jwtTokenProvider),
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {

        http.authenticationProvider(authenticationProvider());

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login", "/register").permitAll()
                .requestMatchers("/certification/**").permitAll()
                .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs", "/v3/api-docs/**",
                        "/v3/api-docs.yaml", "/v3/api-docs.yml")
                .permitAll()

                // static
                .requestMatchers("/css/**").permitAll()
                .requestMatchers("/403", "/404", "/error").permitAll()
                .requestMatchers("/js/**").permitAll()
                .requestMatchers("/images/**").permitAll()
                .requestMatchers("/assets/**").permitAll()

                // upload profile image (logged in)
                .requestMatchers("/uploadProfileImage").authenticated()

                // admin paths
                .requestMatchers("/admin/**").hasRole("ADMIN")

                .anyRequest().authenticated());

        http.formLogin(login -> login
                .loginPage("/login")
                .defaultSuccessUrl("/")
                .permitAll());

        http.logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/"));

        // Enable CSRF with cookie repository para que el token esté disponible en
        // templates
        http.csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()));

        return http.build();
    }
}