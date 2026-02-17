package com.example.cybercert;


import org.springframework.ui.Model;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

@Controller
public class CyberController {

    private final CyberCertApplication cyberCertApplication;
    @Autowired
    private UserRepository userRepository;


    CyberController(CyberCertApplication cyberCertApplication) {
        this.cyberCertApplication = cyberCertApplication;
    }


    @GetMapping("/")
    public String index(Model model, HttpSession session) {
        model.addAttribute("pageCss", "index");
          if (session.getAttribute("user") != null) {
        model.addAttribute("logged", true);
    }
        return "index"; 
    }
    @GetMapping("/login")
    public String login(Model model,HttpSession session) {
        model.addAttribute("pageCss", "auth");
        if(session.getAttribute("user") != null){
            model.addAttribute("logged", true);
        }
        return "login";
    }
@PostMapping("/login") // Login DB
public String loginUser(@RequestParam String username, 
                        @RequestParam String password, 
                        Model model, HttpSession session) {
    
    model.addAttribute("pageCss", "auth");
    Optional<User> optionalUser = userRepository.findByUsername(username);
   
    if (optionalUser.isPresent()) {
        User user = optionalUser.get();
        if (user.getPassword().equals(password)) {
            session.setAttribute("user", user);
        
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
public String showRegisterForm(Model model, HttpSession session) {
    model.addAttribute("user", new User());
    model.addAttribute("pageCss", "auth");
    if (session.getAttribute("user") != null) {
    model.addAttribute("logged", true);
}

    return "register";
}

@PostMapping("/register") //Registration DB 
public String registerUser(@ModelAttribute User user, Model model, HttpSession session ) {
    model.addAttribute("pageCss", "auth");
    Optional<User> optionalUser = userRepository.findByUsername(user.getUsername());

    if(!optionalUser.isPresent()){

        userRepository.save(user);
        session.setAttribute("user", user);
        return "redirect:/";
    }else{

        model.addAttribute("error", "Username alredy exists");
        return "register";  
    }


}


    @GetMapping("/admin")
    public String admin(Model model, HttpSession session) {
        model.addAttribute("pageCss", "admin");
         if (session.getAttribute("user") != null) {
        model.addAttribute("logged", true);
        }

        return "admin"; 
    }

    @GetMapping("/profile")
    public String profile(Model model, HttpSession session) {
        model.addAttribute("pageCss", "profile");
         if (session.getAttribute("user") != null) {
        model.addAttribute("logged", true);}
        return "profile"; 
    }

    @GetMapping("/certification")
    public String certification(Model model, HttpSession session) {
        model.addAttribute("pageCss", "certification");
         if (session.getAttribute("user") != null) {
        model.addAttribute("logged", true);}
        return "certification";
    }
    @GetMapping("/checkout")
    public String checkout(Model model, HttpSession session) {
        model.addAttribute("pageCss", "checkout");
         if (session.getAttribute("user") != null) {
        model.addAttribute("logged", true);}
        return "checkout"; 
    }
    @GetMapping("/shoppingcart")
    public String shoppingcart(Model model, HttpSession session) {
        model.addAttribute("pageCss", "shoping-cart");
         if (session.getAttribute("user") != null) {
        model.addAttribute("logged", true);}
        return "shopping-cart"; 
    }
    @GetMapping("/logout")
public String logout(HttpSession session) {
    session.invalidate();
    return "redirect:/";
}

}

