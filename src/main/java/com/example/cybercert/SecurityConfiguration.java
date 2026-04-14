package com.example.cybercert;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Autowired
    private RepositoryUserDetailsService userDetailsService;

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
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http.authenticationProvider(authenticationProvider());

        http
                .securityMatcher("/api/**");
        // .exceptionHandling(handling ->
        // handling.authenticationEntryPoint(unauthorizedHandlerJwt));

        http
                .authorizeHttpRequests(authorize -> authorize
                        // PRIVATE ENDPOINTS
                        // Images
                        // .requestMatchers(HttpMethod.PUT, "/api/images/*/media").hasRole("USER")
                        // .requestMatchers(HttpMethod.DELETE, "/api/books/*/images/*").hasRole("USER")
                        // Books
                        // .requestMatchers(HttpMethod.POST, "/api/books/**").hasRole("USER")
                        // .requestMatchers(HttpMethod.PUT, "/api/books/**").hasRole("USER")
                        // .requestMatchers(HttpMethod.DELETE, "/api/books/**").hasRole("ADMIN")
                        // Shops
                        // .requestMatchers(HttpMethod.PUT, "/api/shops/**").hasRole("ADMIN")
                        // .requestMatchers(HttpMethod.PUT, "/api/shops/**").hasRole("ADMIN")
                        // .requestMatchers(HttpMethod.DELETE, "/api/shops/**").hasRole("ADMIN")
                        // PUBLIC ENDPOINTS
                        .anyRequest().permitAll());

        // Disable Form login Authentication
        http.formLogin(formLogin -> formLogin.disable());

        // Disable CSRF protection (it is difficult to implement in REST APIs)
        http.csrf(csrf -> csrf.disable());

        // Disable Basic Authentication
        http.httpBasic(httpBasic -> httpBasic.disable());

        // Stateless session
        http.sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Add JWT Token filter
        // http.addFilterBefore(new JwtRequestFilter(userDetailService,
        // jwtTokenProvider),
        // UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {

        http.authenticationProvider(authenticationProvider());

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login", "/register").permitAll()
                .requestMatchers("/certification/**").permitAll()

                // recursos estáticos
                .requestMatchers("/css/**").permitAll()
                .requestMatchers("/403", "/404", "/error").permitAll()
                .requestMatchers("/js/**").permitAll()
                .requestMatchers("/images/**").permitAll()
                .requestMatchers("/assets/**").permitAll()

                // upload de imagen de perfil (logged in)
                .requestMatchers("/uploadProfileImage").authenticated()

                // rutas protegidas
                .requestMatchers("/admin/**").hasRole("ADMIN")

                .anyRequest().authenticated());

        http.formLogin(login -> login
                .loginPage("/login")
                .defaultSuccessUrl("/")
                .permitAll());

        http.logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/"));

        return http.build();
    }
}