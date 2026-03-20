package com.example.cybercert.Controllers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.cybercert.Models.Certification;
import com.example.cybercert.Models.User;
import com.example.cybercert.Role;
import com.example.cybercert.Services.CertificationService;
import com.example.cybercert.Services.ShoppingCartService;
import com.example.cybercert.Services.UserService;

@Controller
public class ShoppingController {

    @Autowired
    private UserService userService;

    @Autowired
    private CertificationService certificationService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @GetMapping("/shoppingcart")
    public String shoppingcart(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        User user = userService.findByUsername(principal.getName()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("pageCss", "shoping-cart");
        model.addAttribute("logged", true);
        model.addAttribute("isAdmin", user.getRole() == Role.ADMIN);

        shoppingCartService.removeOwnedCertificationsFromCart(user.getId());

        List<Certification> certifications = shoppingCartService.getCartCertifications(user.getId());
        List<CartItemView> cartItems = new ArrayList<>();
        for (Certification certification : certifications) {
            cartItems.add(new CartItemView(
                    certification.getId(),
                    certification.getName(),
                    BigDecimal.valueOf(ShoppingCartService.CERTIFICATION_PRICE).setScale(2, RoundingMode.HALF_UP)));
        }

        BigDecimal subtotal = BigDecimal.valueOf(ShoppingCartService.CERTIFICATION_PRICE)
                .multiply(BigDecimal.valueOf(cartItems.size()))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal discount = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.subtract(discount).setScale(2, RoundingMode.HALF_UP);

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("discount", discount);
        model.addAttribute("total", total);
        model.addAttribute("cartSize", cartItems.size());
        model.addAttribute("hasCartItems", !cartItems.isEmpty());

        return "shopping-cart";
    }

    @PostMapping("/shoppingcart/add/{id}")
    public String addToCart(@PathVariable Long id, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        User user = userService.findByUsername(principal.getName()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        Certification certification = certificationService.findById(id).orElse(null);
        if (certification == null) {
            return "redirect:/";
        }

        if (shoppingCartService.hasPurchasedCertification(user.getId(), id)) {
            return "redirect:/certification/" + id;
        }

        shoppingCartService.addToCart(user, certification);
        return "redirect:/shoppingcart";
    }

    @PostMapping("/shoppingcart/remove/{id}")
    public String removeFromCart(@PathVariable Long id, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        User user = userService.findByUsername(principal.getName()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        shoppingCartService.removeFromCart(user.getId(), id);
        return "redirect:/shoppingcart";
    }

    @GetMapping("/checkout")
    public String checkout(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        User user = userService.findByUsername(principal.getName()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("pageCss", "checkout");
        model.addAttribute("logged", true);
        model.addAttribute("isAdmin", user.getRole() == Role.ADMIN);

        shoppingCartService.removeOwnedCertificationsFromCart(user.getId());

        List<Certification> certifications = shoppingCartService.getCartCertifications(user.getId());
        if (certifications.isEmpty()) {
            return "redirect:/shoppingcart";
        }

        List<CartItemView> cartItems = new ArrayList<>();
        for (Certification certification : certifications) {
            cartItems.add(new CartItemView(
                    certification.getId(),
                    certification.getName(),
                    BigDecimal.valueOf(ShoppingCartService.CERTIFICATION_PRICE).setScale(2, RoundingMode.HALF_UP)));
        }

        BigDecimal subtotal = BigDecimal.valueOf(ShoppingCartService.CERTIFICATION_PRICE)
                .multiply(BigDecimal.valueOf(cartItems.size()))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal discount = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.subtract(discount).setScale(2, RoundingMode.HALF_UP);

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("discount", discount);
        model.addAttribute("total", total);
        model.addAttribute("cartSize", cartItems.size());
        model.addAttribute("hasCartItems", !cartItems.isEmpty());

        return "checkout";
    }

    @PostMapping("/checkout")
    public String completeCheckout(Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        User user = userService.findByUsername(principal.getName()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        shoppingCartService.completeCheckout(user);
        return "redirect:/profile";
    }

    public record CartItemView(Long id, String name, BigDecimal price) {
    }
}
