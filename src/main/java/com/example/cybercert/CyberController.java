package com.example.cybercert;



import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CyberController {

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("pageCss", "index");
        return "index"; 
    }
    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("pageCss", "auth");
        return "login"; 
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("pageCss", "auth");
        return "register"; 
    }

    @GetMapping("/admin")
    public String admin(Model model) {
        model.addAttribute("pageCss", "admin");
        return "admin"; 
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        model.addAttribute("pageCss", "profile");
        return "profile";
    }

    @GetMapping("/certification")
    public String certification(Model model) {
        model.addAttribute("pageCss", "certification");
        return "certification"; 
    }
    @GetMapping("/checkout")
    public String checkout(Model model) {
        model.addAttribute("pageCss", "checkout");
        return "checkout"; 
    }
    @GetMapping("/shoppingcart")
    public String shoppingcart(Model model) {
        model.addAttribute("pageCss", "shoping-cart");
        return "shopping-cart"; 
    }
}

