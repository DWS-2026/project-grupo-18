package com.example.cybercert;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
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
        DaoAuthenticationProvider authProvider =
                new DaoAuthenticationProvider(userDetailsService);

        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    http.authenticationProvider(authenticationProvider());

    http.authorizeHttpRequests(auth -> auth
            .requestMatchers("/", "/login", "/register").permitAll()
            .requestMatchers("/certification/**").permitAll()

            // recursos estáticos
            .requestMatchers("/css/**").permitAll()
            .requestMatchers("/js/**").permitAll()
            .requestMatchers("/images/**").permitAll()
            .requestMatchers("/assets/**").permitAll()

            // rutas protegidas
            .requestMatchers("/admin").hasRole("ADMIN")

            .anyRequest().authenticated()
    );

    http.formLogin(login -> login
            .loginPage("/login")
            .defaultSuccessUrl("/")
            .permitAll()
    );

    http.logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/")
    );

    http.csrf(csrf -> csrf.disable());

    return http.build();
}
}