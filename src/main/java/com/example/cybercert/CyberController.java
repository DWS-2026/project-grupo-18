package com.example.cybercert;


import org.springframework.ui.Model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class CyberController {
    @Autowired
    private UserRepository userRepository;


    @GetMapping("/")
    public String index() {
        return "index"; // Spring busca index.html en templates
    }
    @GetMapping("/login")
public String login() {
    return "login";
}

@GetMapping("/register")
public String showRegisterForm(Model model) {
    model.addAttribute("user", new User());
    return "register";
}

@PostMapping("/register")
public String registerUser(@ModelAttribute User user) {

    userRepository.save(user);

    return "redirect:/login";
}


    @GetMapping("/admin")
    public String admin() {
        return "admin"; // renderiza admin.html
    }

    @GetMapping("/profile")
    public String profile() {
        return "profile"; // renderiza profile.html
    }

    @GetMapping("/certification")
    public String certification() {
        return "certification"; // renderiza certification.html
    }
    @GetMapping("/checkout")
    public String checkout() {
        return "checkout"; // renderiza certification.html
    }
    @GetMapping("/shoppingcart")
    public String shoppingcart() {
        return "shopping-cart"; // renderiza certification.html
    }
}

