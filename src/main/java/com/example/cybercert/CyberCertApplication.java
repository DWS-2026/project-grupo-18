package com.example.cybercert;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.cybercert.Models.Certification;
import com.example.cybercert.Models.User;
import com.example.cybercert.Repositories.CertificationRepository;
import com.example.cybercert.Repositories.UserRepository;

import javax.sql.rowset.serial.SerialBlob;
import java.io.InputStream;
import java.sql.Blob;
import java.util.List;

@SpringBootApplication
@ComponentScan(basePackages = "com.example.cybercert")
public class CyberCertApplication {

        public static void main(String[] args) {
                SpringApplication.run(CyberCertApplication.class, args);
        }

}