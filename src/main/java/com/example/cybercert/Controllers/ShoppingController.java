package com.example.cybercert.Controllers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

import jakarta.servlet.http.HttpSession;

@Controller
public class ShoppingController {

    private static final BigDecimal CERTIFICATION_PRICE = new BigDecimal("89.99");

    @Autowired
    private UserService userService;

    @Autowired
    private CertificationService certificationService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @GetMapping("/shoppingcart")
    public String shoppingcart(Model model, Principal principal, HttpSession session) {
        model.addAttribute("pageCss", "shoping-cart");

        User user = getAuthenticatedUser(principal, model);
        List<CartItemView> cartItems = buildCartItems(user, session);
        fillCartSummary(model, cartItems.size());
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("hasCartItems", !cartItems.isEmpty());

        return "shopping-cart";
    }

    @PostMapping("/shoppingcart/add/{id}")
    public String addToCart(@PathVariable Long id, Principal principal, HttpSession session) {
        User user = getAuthenticatedUser(principal, null);
        if (user == null) {
            return "redirect:/login";
        }

        Certification certification = certificationService.findById(id).orElse(null);
        if (certification == null || user.hasCertification(id)) {
            return "redirect:/certification/" + id;
        }

        shoppingCartService.addToCart(session, id);
        return "redirect:/shoppingcart";
    }

    @PostMapping("/shoppingcart/remove/{id}")
    public String removeFromCart(@PathVariable Long id, HttpSession session) {
        shoppingCartService.removeFromCart(session, id);
        return "redirect:/shoppingcart";
    }

    @GetMapping("/checkout")
    public String checkout(Model model, Principal principal, HttpSession session) {
        model.addAttribute("pageCss", "checkout");

        User user = getAuthenticatedUser(principal, model);
        List<CartItemView> cartItems = buildCartItems(user, session);

        if (cartItems.isEmpty()) {
            return "redirect:/shoppingcart";
        }

        fillCartSummary(model, cartItems.size());
        model.addAttribute("cartItems", cartItems);

        return "checkout";
    }

    @PostMapping("/checkout")
    public String completeCheckout(Principal principal, HttpSession session) {
        User user = getAuthenticatedUser(principal, null);
        if (user == null) {
            return "redirect:/login";
        }

        Set<Long> cartIds = shoppingCartService.getCartIds(session);
        if (cartIds.isEmpty()) {
            return "redirect:/shoppingcart";
        }

        boolean updated = false;
        for (Long certificationId : cartIds) {
            Certification certification = certificationService.findById(certificationId).orElse(null);
            if (certification != null && !user.hasCertification(certificationId)) {
                user.addCertification(certification);
                updated = true;
            }
        }

        if (updated) {
            userService.save(user);
        }

        shoppingCartService.clear(session);
        return "redirect:/profile";
    }

    private User getAuthenticatedUser(Principal principal, Model model) {
        if (principal == null) {
            return null;
        }

        User user = userService.findByUsername(principal.getName()).orElse(null);
        if (user != null && model != null) {
            model.addAttribute("logged", true);
            model.addAttribute("isAdmin", user.getRole() == Role.ADMIN);
        }

        return user;
    }

    private List<CartItemView> buildCartItems(User user, HttpSession session) {
        Set<Long> cartIds = shoppingCartService.getCartIds(session);
        List<CartItemView> cartItems = new ArrayList<>();
        List<Long> idsToRemove = new ArrayList<>();

        for (Long certificationId : cartIds) {
            if (user != null && user.hasCertification(certificationId)) {
                idsToRemove.add(certificationId);
                continue;
            }

            Certification certification = certificationService.findById(certificationId).orElse(null);
            if (certification != null) {
                cartItems.add(new CartItemView(certification.getId(), certification.getName(), CERTIFICATION_PRICE));
            } else {
                idsToRemove.add(certificationId);
            }
        }

        for (Long certificationId : idsToRemove) {
            shoppingCartService.removeFromCart(session, certificationId);
        }

        return cartItems;
    }

    private void fillCartSummary(Model model, int itemsCount) {
        BigDecimal subtotal = CERTIFICATION_PRICE.multiply(BigDecimal.valueOf(itemsCount));
        BigDecimal discount = BigDecimal.ZERO;
        BigDecimal total = subtotal.subtract(discount);

        model.addAttribute("subtotal", subtotal.setScale(2, RoundingMode.HALF_UP));
        model.addAttribute("discount", discount.setScale(2, RoundingMode.HALF_UP));
        model.addAttribute("total", total.setScale(2, RoundingMode.HALF_UP));
        model.addAttribute("cartSize", itemsCount);
        model.addAttribute("hasCartItems", itemsCount > 0);
    }

    public record CartItemView(Long id, String name, BigDecimal price) {
    }
}
