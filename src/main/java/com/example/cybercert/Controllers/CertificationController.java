package com.example.cybercert.Controllers;

import java.security.Principal;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.cybercert.Models.Certification;
import com.example.cybercert.Models.User;
import com.example.cybercert.Role;
import com.example.cybercert.Services.CertificationService;
import com.example.cybercert.Services.CommentService;
import com.example.cybercert.Services.ShoppingCartService;
import com.example.cybercert.Services.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class CertificationController {

    @Autowired
    private UserService userService;

    @Autowired
    private CertificationService certificationService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @GetMapping("/certification/{id}")
    public String certification(@PathVariable Long id, Model model, Principal principal, HttpSession session) {
        model.addAttribute("pageCss", "certification");

        User user = getAuthenticatedUser(principal, model);
        Certification cert = certificationService.findById(id)
                .orElseThrow(() -> new RuntimeException("Certification not found"));

        Set<Long> cartIds = shoppingCartService.getCartIds(session);
        boolean alreadyOwned = user != null && user.hasCertification(id);
        boolean alreadyInCart = cartIds.contains(id);
        long visibleCartItems = cartIds.stream()
            .filter(certificationId -> user == null || !user.hasCertification(certificationId))
            .count();

        model.addAttribute("certification", cert);
        model.addAttribute("comments", commentService.getCommentsByCertification(id));
        model.addAttribute("alreadyOwned", alreadyOwned);
        model.addAttribute("alreadyInCart", alreadyInCart);
        model.addAttribute("cartSize", visibleCartItems);
        model.addAttribute("hasCartItems", visibleCartItems > 0);

        return "certification";
    }

    @PostMapping("/certification/{id}/comment")
    public String addComment(@PathVariable Long id,
            Principal principal,
            @RequestParam("text") String text) {

        if (principal == null) {
            return "redirect:/login";
        }

        User user = userService.findByUsername(principal.getName()).orElse(null);
        Certification certification = certificationService.findById(id).orElse(null);

        if (user == null || certification == null || text == null || text.trim().isEmpty()) {
            return "redirect:/certification/" + id;
        }

        commentService.addComment(user, certification, text.trim());

        return "redirect:/certification/" + id;
    }

    @PostMapping("/certification/{id}/remove-comment")
    public String removeComment(@PathVariable Long id) {
        return "redirect:/certification/" + id;
    }

    private User getAuthenticatedUser(Principal principal, Model model) {
        if (principal == null) {
            return null;
        }

        User user = userService.findByUsername(principal.getName()).orElse(null);
        if (user != null) {
            model.addAttribute("logged", true);
            model.addAttribute("isAdmin", user.getRole() == Role.ADMIN);
        }

        return user;
    }
}
