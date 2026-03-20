package com.example.cybercert.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

import com.example.cybercert.Role;
import com.example.cybercert.Models.Certification;
import com.example.cybercert.Models.User;
import com.example.cybercert.Services.CertificationService;
import com.example.cybercert.Services.CommentService;
import com.example.cybercert.Services.UserService;

import java.security.Principal;

import java.io.InputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Principal;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;

import jakarta.transaction.Transactional;

@Controller
public class CyberController {

    @Autowired
    private UserService userService;

    @Autowired
    private CertificationService certificationService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // HOME
    @GetMapping("/")
    public String index(Model model, Principal principal) {

        model.addAttribute("pageCss", "index");

        if (principal != null) {
            model.addAttribute("logged", true);

            User user = userService.findByUsername(principal.getName()).orElse(null);

            if (user != null) {
                model.addAttribute("isAdmin", user.getRole() == Role.ADMIN);
            }
        }
        List<Certification> certifications = certificationService.findAll();
        certifications.removeIf(certification -> certification.getImage() == null || certification.getImage().getId() == null);
        model.addAttribute("certifications", certifications);

        return "index";
    }

    

    
    

    
    

    

    

    

   
    
}
