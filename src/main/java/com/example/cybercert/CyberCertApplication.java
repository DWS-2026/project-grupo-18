package com.example.cybercert;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class CyberCertApplication {

    public static void main(String[] args) {
        SpringApplication.run(CyberCertApplication.class, args);
    }
    @Autowired
    PasswordEncoder passwordEncoder;
    @Bean
    CommandLineRunner init(UserRepository userRepository) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {

                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setEmail("admin@cybercert.com");
                admin.setRole(Role.ADMIN);

                userRepository.save(admin);

                System.out.println("ADMIN CREADO");
            }
        };
    }
}

