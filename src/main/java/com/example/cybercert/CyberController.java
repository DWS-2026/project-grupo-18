package com.example.cybercert;



import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CyberController {

    @GetMapping("/")
    public String index() {
        return "index"; // Spring busca index.html en templates
    }
    @GetMapping("/login")
    public String login() {
        return "login"; // renderiza login.html
    }

    @GetMapping("/register")
    public String register() {
        return "register"; // renderiza register.html
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
    @GetMapping("/chekout")
    public String checkout() {
        return "checkout"; // renderiza certification.html
    }
    @GetMapping("/shoppingcart")
    public String shoppingcart() {
        return "shopping-cart"; // renderiza certification.html
    }
}

