package com.example.cybercert.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import com.example.cybercert.Role;
import com.example.cybercert.Models.User;
import com.example.cybercert.Models.UserCertification;
import com.example.cybercert.Repositories.UserCertificationRepository;
import com.example.cybercert.Services.ShoppingCartService;
import com.example.cybercert.Services.UserService;

import java.security.Principal;

import java.util.Optional;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserCertificationRepository userCertificationRepository;

    @Autowired
    private ShoppingCartService shoppingCartService;

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

        Optional<User> optionalUser = userService.findByUsername(user.getUsername());

        if (optionalUser.isEmpty()) {
            Optional<User> optionalUser1 = userService.findByEmail(user.getEmail());
            if (optionalUser1.isPresent()) {
                model.addAttribute("error", "Email already exists");
                return "register";
            }
            user.setRole(Role.USER);

            // CIFRAR PASSWORD
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            userService.save(user);

            return "redirect:/login";

        } else {

            model.addAttribute("error", "Username already exists");
            return "register";
        }
    }

    // PROFILE PAGE
    @GetMapping("/profile")
    public String profile(Model model, Principal principal) {

        if (principal == null) {
            return "redirect:/login";
        }

        User user = userService.findByUsername(principal.getName()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("pageCss", "profile");
        model.addAttribute("logged", true);
        model.addAttribute("user", user);
        model.addAttribute("isAdmin", user.getRole() == Role.ADMIN);
        model.addAttribute("cartSize", shoppingCartService.getVisibleCartSize(user.getId()));
        model.addAttribute("hasCartItems", shoppingCartService.getVisibleCartSize(user.getId()) > 0);

        List<UserCertification> ownedCertifications = userCertificationRepository
                .findByUserIdOrderByPurchasedAtDesc(user.getId());
        List<OwnedCertificationView> ownedCertificationViews = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (UserCertification ownedCertification : ownedCertifications) {
            if (ownedCertification.getCertification() != null) {
                String purchasedAt = "";
                if (ownedCertification.getPurchasedAt() != null) {
                    purchasedAt = ownedCertification.getPurchasedAt().format(formatter);
                }

                ownedCertificationViews.add(new OwnedCertificationView(
                        ownedCertification.getCertification().getId(),
                        ownedCertification.getCertification().getName(),
                        purchasedAt));
            }
        }

        model.addAttribute("ownedCertifications", ownedCertificationViews);
        model.addAttribute("hasOwnedCertifications", !ownedCertificationViews.isEmpty());

        return "profile";
    }

    // EDIT PROFILE PAGE
    @GetMapping("/edit")
    public String editPage(Model model, Principal principal) {

        if (principal != null) {
            model.addAttribute("logged", true);

            User user = userService.findByUsername(principal.getName()).orElse(null);

            if (user != null) {
                model.addAttribute("isAdmin", user.getRole() == Role.ADMIN);
            }
        }

        if (principal == null) {
            return "redirect:/login";
        }

        User user = userService.findByUsername(principal.getName()).orElse(null);

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

        User user = userService.findByUsername(principal.getName()).orElse(null);

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

            userService.save(user);
        }

        return "redirect:/profile";
    }

    // RESET PAGE
    @GetMapping("/reset")
    public String reset(Model model, Principal principal) {

        if (principal != null) {
            model.addAttribute("logged", true);

            User user = userService.findByUsername(principal.getName()).orElse(null);

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

        User user = userService.findByUsername(principal.getName()).orElse(null);
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
        userService.save(user);

        return "redirect:/profile";
    }

    @PostMapping("/uploadProfileImage")
    public String uploadProfileImage(@RequestParam("image") MultipartFile file,
            Principal principal) throws IOException {

        User user = userService.findByUsername(principal.getName()).get();

        String uploadDir = "src/main/resources/static/uploads/";

        String fileName = user.getId() + ".png";

        Path path = Paths.get(uploadDir + fileName);

        Files.write(path, file.getBytes());

        user.setProfileImage("/uploads/" + fileName);

        userService.save(user);

        return "redirect:/profile";
    }

    public record OwnedCertificationView(Long id, String name, String purchasedAt) {
    }

}
