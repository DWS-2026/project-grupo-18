package com.example.cybercert;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.transaction.Transactional;


@Controller
public class CyberController {

    @Autowired
    private UserRepository userRepository;

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

        model.addAttribute("users", users);

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

            User user = userRepository.findByUsername(principal.getName()).orElse(null);

            model.addAttribute("logged", true);
            model.addAttribute("user", user);
        }

        return "profile";
    }


    // CERTIFICATION PAGE
    @GetMapping("/certification")
    public String certification(Model model, Principal principal) {

        model.addAttribute("pageCss", "certification");

        if (principal != null) {
            model.addAttribute("logged", true);
        }

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
        }

        return "shopping-cart";
    }


    // EDIT PROFILE PAGE
    @GetMapping("/edit")
    public String editPage(Model model, Principal principal) {

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
    public String reset(Model model) {

        model.addAttribute("pageCss", "profile");

        return "reset";
    }

    @PostMapping("/admin/delete")
    @Transactional
    public String deleteUser(@RequestParam String username) {
        
        if(!"admin".equals(username)){

            userRepository.deleteByUsername(username);
        
        }

        return "redirect:/admin";
    }
    @GetMapping("/403")
public String error403(){
    return "error403";
}
    
}
