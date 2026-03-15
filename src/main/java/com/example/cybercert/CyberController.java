package com.example.cybercert;

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
    private UserRepository userRepository;

    @Autowired
    private CertificationRepository certificationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // HOME
    @GetMapping("/")
    public String index(Model model, Principal principal) {

        model.addAttribute("pageCss", "index");

        if (principal != null) {
            model.addAttribute("logged", true);

            User user = userRepository.findByUsername(principal.getName()).orElse(null);

            if (user != null) {
                model.addAttribute("isAdmin", user.getRole() == Role.ADMIN);
            }
        }
        List<Certification> certifications = certificationRepository.findAll();
        model.addAttribute("certifications", certifications);

        return "index";
    }

    // LOGIN PAGE
    @GetMapping("/login")
    public String login(Model model) {

        model.addAttribute("pageCss", "auth");

        return "login";
    }

    // REGISTER PAGE
    @GetMapping("/register")
    public String showRegisterForm(Model model) {

        model.addAttribute("user", new User());
        model.addAttribute("pageCss", "auth");

        return "register";
    }

    // REGISTER USER
    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, Model model) {

        model.addAttribute("pageCss", "auth");

        Optional<User> optionalUser = userRepository.findByUsername(user.getUsername());

        if (optionalUser.isEmpty()) {
            Optional<User> optionalUser1 = userRepository.findByEmail(user.getEmail());
            if (optionalUser1.isPresent()) {
                model.addAttribute("error", "Email already exists");
                return "register";
            }
            user.setRole(Role.USER);

            // CIFRAR PASSWORD
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            userRepository.save(user);

            return "redirect:/login";

        } else {

            model.addAttribute("error", "Username already exists");
            return "register";
        }
    }

    // ADMIN PAGE
    @GetMapping("/admin")
    public String admin(Model model, Principal principal) {

        List<User> users = userRepository.findAll();
        List<Certification> certifications = certificationRepository.findAll();

        model.addAttribute("users", users);
        model.addAttribute("certifications", certifications);

        model.addAttribute("pageCss", "admin");

        if (principal != null) {
            model.addAttribute("logged", true);
        }

        return "admin";
    }

    // PROFILE PAGE
    @GetMapping("/profile")
    public String profile(Model model, Principal principal) {

        model.addAttribute("pageCss", "profile");

        if (principal != null) {
            model.addAttribute("logged", true);

            User user = userRepository.findByUsername(principal.getName()).orElse(null);

            if (user != null) {
                model.addAttribute("isAdmin", user.getRole() == Role.ADMIN);
            }
        }

        if (principal != null) {

            User user = userRepository.findByUsername(principal.getName()).orElse(null);

            model.addAttribute("logged", true);
            model.addAttribute("user", user);
        }

        return "profile";
    }

    // CERTIFICATION PAGE
    @GetMapping("/certification/{id}")
    public String certification(@PathVariable Long id, Model model, Principal principal) {

        if (principal != null) {
            model.addAttribute("logged", true);

            User user = userRepository.findByUsername(principal.getName()).orElse(null);

            if (user != null) {
                model.addAttribute("isAdmin", user.getRole() == Role.ADMIN);
            }
        }

        model.addAttribute("pageCss", "certification");
        Certification cert = certificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Certification not found"));

        model.addAttribute("certification", cert);
        return "certification";
    }

    // CHECKOUT
    @GetMapping("/checkout")
    public String checkout(Model model, Principal principal) {

        model.addAttribute("pageCss", "checkout");
        

        if (principal != null) {
            model.addAttribute("logged", true);
        }

        return "checkout";
    }

    // SHOPPING CART
    @GetMapping("/shoppingcart")
    public String shoppingcart(Model model, Principal principal) {

        model.addAttribute("pageCss", "shoping-cart");

        if (principal != null) {
            model.addAttribute("logged", true);

            User user = userRepository.findByUsername(principal.getName()).orElse(null);

            if (user != null) {
                model.addAttribute("isAdmin", user.getRole() == Role.ADMIN);
            }
        }

        if (principal != null) {
            model.addAttribute("logged", true);
        }

        return "shopping-cart";
    }

    // EDIT PROFILE PAGE
    @GetMapping("/edit")
    public String editPage(Model model, Principal principal) {


        if (principal != null) {
            model.addAttribute("logged", true);

            User user = userRepository.findByUsername(principal.getName()).orElse(null);

            if (user != null) {
                model.addAttribute("isAdmin", user.getRole() == Role.ADMIN);
            }
        }

        if (principal == null) {
            return "redirect:/login";
        }

        User user = userRepository.findByUsername(principal.getName()).orElse(null);

        model.addAttribute("user", user);
        model.addAttribute("pageCss", "profile");

        return "edit";
    }

    // EDIT USER DATA
    @PostMapping("/edit")
    public String editUser(Model model,
            Principal principal,
            String username,
            String email,
            String password) {

        model.addAttribute("pageCss", "profile");

        if (principal == null) {
            return "redirect:/login";
        }

        User user = userRepository.findByUsername(principal.getName()).orElse(null);

        if (user != null) {

            if (username != null && !username.isEmpty()) {
                user.setUsername(username);
            }

            if (email != null && !email.isEmpty()) {
                user.setEmail(email);
            }

            if (password != null && !password.isEmpty()) {

                // CIFRAR PASSWORD NUEVA
                user.setPassword(passwordEncoder.encode(password));
            }

            userRepository.save(user);
        }

        return "redirect:/profile";
    }

    // RESET PAGE
    @GetMapping("/reset")
    public String reset(Model model, Principal principal) {

        if (principal != null) {
            model.addAttribute("logged", true);

            User user = userRepository.findByUsername(principal.getName()).orElse(null);

            if (user != null) {
                model.addAttribute("isAdmin", user.getRole() == Role.ADMIN);
            }
        }

        model.addAttribute("pageCss", "profile");

        return "reset";
    }

    @PostMapping("/reset")
    public String resetPassword(Model model,
                                Principal principal,
                                @RequestParam String actual_password,
                                @RequestParam String new_password) {

        if (principal == null) {
            return "redirect:/login";
        }

        User user = userRepository.findByUsername(principal.getName()).orElse(null);
        model.addAttribute("pageCss", "profile");

        if (user == null) {
            return "redirect:/login";
        }

        if (!passwordEncoder.matches(actual_password, user.getPassword())) {
            model.addAttribute("error", "Current password is incorrect.");
            model.addAttribute("logged", true);
            model.addAttribute("user", user);
            model.addAttribute("isAdmin", user.getRole() == Role.ADMIN);
            return "reset";
        }

        if (new_password == null || new_password.isEmpty()) {
            model.addAttribute("error", "New password cannot be empty.");
            model.addAttribute("logged", true);
            model.addAttribute("user", user);
            model.addAttribute("isAdmin", user.getRole() == Role.ADMIN);
            return "reset";
        }

        user.setPassword(passwordEncoder.encode(new_password));
        userRepository.save(user);

        return "redirect:/profile";
    }

    @PostMapping("/admin/delete")
    @Transactional
    public String deleteUser(@RequestParam String username) {

        if (!"admin".equals(username)) {

            userRepository.deleteByUsername(username);

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

            User user = userRepository.findByUsername(principal.getName()).orElse(null);

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

        if (imageFile != null && !imageFile.isEmpty()) {
            String uploadDir = "src/main/resources/static/assets/img/";
            String fileName = imageFile.getOriginalFilename();
            Path path = Paths.get(uploadDir + fileName);
            Files.write(path, imageFile.getBytes());
        }

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
                "assets/img/" + imageFile.getOriginalFilename());

        certificationRepository.save(cert);

        return "redirect:/admin";
    }

    @PostMapping("/admin/delete-certi")
    @Transactional
    public String deleteCerti(@RequestParam("certId") Long certId) {
        if (certificationRepository.existsById(certId)) {
            certificationRepository.deleteById(certId);
        }
        return "redirect:/admin";
    }

    @PostMapping("/uploadProfileImage")
    public String uploadProfileImage(@RequestParam("image") MultipartFile file,
            Principal principal) throws IOException {

        User user = userRepository.findByUsername(principal.getName()).get();

        String uploadDir = "src/main/resources/static/uploads/";

        String fileName = user.getId() + ".png";

        Path path = Paths.get(uploadDir + fileName);

        Files.write(path, file.getBytes());

        user.setProfileImage("/uploads/" + fileName);

        userRepository.save(user);

        return "redirect:/profile";
    }
}
