package com.example.cybercert;


import org.springframework.ui.Model;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
@PostMapping("/login") // Login DB
public String loginUser(@RequestParam String username, 
                        @RequestParam String password, 
                        Model model) {
    
    Optional<User> optionalUser = userRepository.findByUsername(username);
   
    if (optionalUser.isPresent()) {
        User user = optionalUser.get();
        if (user.getPassword().equals(password)) {
        
            return "redirect:/profile"; 
        } else {
            
            model.addAttribute("error", "Invalid Credentials");
            return "login";
        }

    }else{
        model.addAttribute("error", "Invalid Credentials");
            return "login";
    }
}


@GetMapping("/register")
public String showRegisterForm(Model model) {
    model.addAttribute("user", new User());
    return "register";
}

@PostMapping("/register") //Registration DB 
public String registerUser(@ModelAttribute User user, Model model ) {

    Optional<User> optionalUser = userRepository.findByUsername(user.getUsername());

    if(!optionalUser.isPresent()){

        userRepository.save(user);
        
    }else{

        model.addAttribute("error", "Username alredy exists");
    }

    return "redirect:/";
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

