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
import com.example.cybercert.Models.Image;
import com.example.cybercert.Models.User;
import com.example.cybercert.Services.CertificationService;
import com.example.cybercert.Services.CommentService;
import com.example.cybercert.Services.ImageService;
import com.example.cybercert.Services.UserService;

import java.security.Principal;

import java.io.InputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Principal;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;

import jakarta.transaction.Transactional;

@Controller
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private CertificationService certificationService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ADMIN PAGE
    @GetMapping("/admin")
    public String admin(Model model, Principal principal) {

        if (principal != null) {
            model.addAttribute("logged", true);

            User user = userService.findByUsername(principal.getName()).orElse(null);

            if (user != null) {
                model.addAttribute("isAdmin", user.getRole() == Role.ADMIN);
            }
        }

        List<User> users = userService.findAll();
        List<Certification> certifications = certificationService.findAll();
        model.addAttribute("users", users);
        model.addAttribute("certifications", certifications);

        model.addAttribute("pageCss", "admin");

        if (principal != null) {
            model.addAttribute("logged", true);
        }

        return "admin";
    }

    @PostMapping("/admin/delete")
    @Transactional
    public String deleteUser(@RequestParam String username) {
        if (!"admin".equals(username)) {
            userService.deleteByUsername(username);
        }

        return "redirect:/admin";
    }

    @GetMapping("/403")
    public String error403() {
        return "error403";
    }

    @GetMapping("/admin/add_certi")
    public String showAddCertificateForm(Model model, Principal principal) {
        model.addAttribute("pageCss", "auth");

        if (principal != null) {
            model.addAttribute("logged", true);

            User user = userService.findByUsername(principal.getName()).orElse(null);

            if (user != null) {
                model.addAttribute("isAdmin", user.getRole() == Role.ADMIN);
            }
        }
        return "add_certi";
    }

    @PostMapping("/admin/add_certi")
    @Transactional
    public String addCertificate(
            @RequestParam String name,
            @RequestParam String level,
            @RequestParam int duration,
            @RequestParam String format,
            @RequestParam String language,
            @RequestParam String description,
            @RequestParam String requirements,
            @RequestParam String contents,
            @RequestParam("imageFile") MultipartFile imageFile) throws IOException {

        List<String> reqList = List.of(requirements.split(","));
        List<String> contList = List.of(contents.split(","));

        Certification cert = new Certification(
                name,
                level,
                duration,
                format,
                language,
                description,
                reqList,
                contList,
                null);

        if (!imageFile.isEmpty()) {
            Image image = imageService.createImage(imageFile.getInputStream());
            cert.setImage(image);
            certificationService.save(cert);
        }

        return "redirect:/admin";
    }

    @GetMapping("/admin/edit-certi/{id}")
    public String showEditCertificateForm(@PathVariable Long id, Model model, Principal principal) {
        Certification cert = certificationService.findById(id)
                .orElseThrow(() -> new RuntimeException("Certification not found"));

        System.out.println("Editing certification: " + cert.getName());
        System.out.println("Certification ID: " + cert.getId());
        System.out.println("Certification Contents: " + cert.getContents());

        model.addAttribute("certification", cert);
        String contentsString = String.join(",", cert.getContents());
        String requirementsString = String.join(",", cert.getRequirements());
        model.addAttribute("contents", contentsString);
        model.addAttribute("requirements", requirementsString);

        switch (cert.getLevel()) {
            case "Beginner":
                model.addAttribute("beginnerSelected", true);
                break;
            case "Intermediate":
                model.addAttribute("intermediateSelected", true);
                break;
            case "Advanced":
                model.addAttribute("advancedSelected", true);
                break;
            default:
                break;
        }

        model.addAttribute("pageCss", "auth");

        if (principal != null) {
            model.addAttribute("logged", true);

            User user = userService.findByUsername(principal.getName()).orElse(null);

            if (user != null) {
                model.addAttribute("isAdmin", user.getRole() == Role.ADMIN);
            }
        }
        return "edit_certi";
    }

    @PostMapping("/admin/edit-certi")
    public String editCertification(
            Principal principal,
            Model model,
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) int duration,
            @RequestParam(required = false) String format,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String requirements,
            @RequestParam(required = false) String contents,
            @RequestParam(required = false, value = "imageFile") MultipartFile imageFile) throws IOException {

        if (principal != null) {
            model.addAttribute("logged", true);

            User user = userService.findByUsername(principal.getName()).orElse(null);

            if (user != null) {
                model.addAttribute("isAdmin", user.getRole() == Role.ADMIN);
            }
        }

        Certification cert = certificationService.findById(id)
                .orElseThrow(() -> new RuntimeException("Certification not found"));
        cert.setName(name);
        cert.setLevel(level);
        cert.setDuration(duration);
        cert.setFormat(format);
        cert.setLanguage(language);
        cert.setDescription(description);

        if (requirements != null) {
            cert.setRequirements(new ArrayList<>(Arrays.asList(requirements.split(","))));
        }

        if (contents != null) {
            cert.setContents(new ArrayList<>(Arrays.asList(contents.split(","))));
        }
        certificationService.save(cert);

        if (!imageFile.isEmpty()) {
            Image image = imageService.createImage(imageFile.getInputStream());
            cert.setImage(image);
            certificationService.save(cert);
        }

        return "redirect:/admin";
    }

    @PostMapping("/admin/delete-certi")
    public String deleteCertification(@RequestParam Long certId) {
        certificationService.deleteById(certId);
        return "redirect:/admin";
    }

}
