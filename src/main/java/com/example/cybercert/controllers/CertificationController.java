package com.example.cybercert.controllers;

import java.net.MalformedURLException;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.cybercert.models.Certification;
import com.example.cybercert.models.User;
import com.example.cybercert.services.CertificationService;
import com.example.cybercert.services.CommentService;
import com.example.cybercert.services.ShoppingCartService;
import com.example.cybercert.services.UserService;
import com.example.cybercert.services.CertificationDocumentService;
import com.example.security.Role;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;

import java.nio.file.Path;
import java.nio.file.Paths;

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

    @Autowired
    private CertificationDocumentService certificationDocumentService;

    @GetMapping("/certification/{id}")
    public String certification(@PathVariable Long id, Model model, Principal principal) {
        model.addAttribute("pageCss", "certification");

        Certification cert = certificationService.findById(id)
                .orElseThrow(() -> new RuntimeException("Certification not found"));

        boolean alreadyOwned = false;
        boolean alreadyInCart = false;
        int visibleCartItems = 0;

        if (principal != null) {
            User user = userService.findByUsername(principal.getName()).orElse(null);
            if (user != null) {
                model.addAttribute("logged", true);
                model.addAttribute("isAdmin", user.getRole() == Role.ADMIN);

                alreadyOwned = shoppingCartService.hasPurchasedCertification(user.getId(), id);
                alreadyInCart = shoppingCartService.isCertificationInCart(user.getId(), id);
                visibleCartItems = shoppingCartService.getVisibleCartSize(user.getId());
            }
        }

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
            RedirectAttributes redirectAttributes,
            Principal principal,
            @RequestParam("text") String text,
            @RequestParam("rating") int rating) {

        if (principal == null) {
            return "redirect:/login";
        }

        User user = userService.findByUsername(principal.getName()).orElse(null);
        Certification certification = certificationService.findById(id).orElse(null);

        if (user == null || certification == null || text == null || text.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Error submitting comment.");
            return "redirect:/certification/" + id;
        }

        if (rating < 1 || rating > 5) {
            redirectAttributes.addFlashAttribute("error", "Rating must be between 1 and 5");
            return "redirect:/certification/" + id;
        }

        commentService.addComment(user, certification, text.trim(), rating);

        return "redirect:/certification/" + id;
    }

    @PostMapping("/certification/{id}/remove-comment")
    public String removeComment(@PathVariable Long id) {
        return "redirect:/certification/" + id;
    }

    @GetMapping("/certification/{id}/document")
    public String getMethodName(@RequestParam String param) {
        return new String();
    }

    @GetMapping("/certification/{id}/download-document")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long id) throws MalformedURLException {

        Certification cert = certificationService.findById(id)
                .orElseThrow(() -> new RuntimeException("Certification not found"));

        if (cert.getDocumentPath() == null) {
            throw new RuntimeException("No document associated with this certification");
        }

        Resource resource = certificationDocumentService.loadDocumentAsResource(cert.getDocumentPath());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/pdf"))
                .body(resource);
    }
}
