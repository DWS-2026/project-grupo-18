package com.example.cybercert.Services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.cybercert.Models.Certification;
import com.example.cybercert.Models.ShoppingCartItem;
import com.example.cybercert.Models.User;
import com.example.cybercert.Models.UserCertification;
import com.example.cybercert.Repositories.ShoppingCartItemRepository;
import com.example.cybercert.Repositories.UserCertificationRepository;

import jakarta.transaction.Transactional;

@Service
public class ShoppingCartService {

    public static final double CERTIFICATION_PRICE = 89.99;

    @Autowired
    private ShoppingCartItemRepository shoppingCartItemRepository;

    @Autowired
    private UserCertificationRepository userCertificationRepository;
    public List<UserCertification> findByUserIdOrderByPurchasedAtDesc(Long userId) {
        return userCertificationRepository.findByUserIdOrderByPurchasedAtDesc(userId);
    }

    public boolean hasPurchasedCertification(Long userId, Long certificationId) {
        if (userId == null || certificationId == null) {
            return false;
        }
        return userCertificationRepository.existsByUserIdAndCertificationId(userId, certificationId);
    }

    public boolean isCertificationInCart(Long userId, Long certificationId) {
        if (userId == null || certificationId == null) {
            return false;
        }
        return shoppingCartItemRepository.existsByUserIdAndCertificationId(userId, certificationId);
    }

    public List<Certification> getCartCertifications(Long userId) {
        List<Certification> certifications = new ArrayList<>();
        if (userId == null) {
            return certifications;
        }

        List<ShoppingCartItem> items = shoppingCartItemRepository.findByUserId(userId);
        for (ShoppingCartItem item : items) {
            if (item.getCertification() != null) {
                certifications.add(item.getCertification());
            }
        }
        return certifications;
    }

    public int getVisibleCartSize(Long userId) {
        return getCartCertifications(userId).size();
    }

    @Transactional
    public void addToCart(User user, Certification certification) {
        if (user == null || certification == null) {
            return;
        }

        if (hasPurchasedCertification(user.getId(), certification.getId())) {
            return;
        }

        if (isCertificationInCart(user.getId(), certification.getId())) {
            return;
        }

        ShoppingCartItem item = new ShoppingCartItem(user, certification, CERTIFICATION_PRICE);
        shoppingCartItemRepository.save(item);
    }

    @Transactional
    public void removeFromCart(Long userId, Long certificationId) {
        if (userId == null || certificationId == null) {
            return;
        }
        shoppingCartItemRepository.deleteByUserIdAndCertificationId(userId, certificationId);
    }

    @Transactional
    public void completeCheckout(User user) {
        if (user == null) {
            return;
        }

        List<ShoppingCartItem> items = shoppingCartItemRepository.findByUserId(user.getId());
        for (ShoppingCartItem item : items) {
            Certification certification = item.getCertification();
            if (certification == null) {
                continue;
            }

            boolean alreadyOwned = userCertificationRepository.existsByUserIdAndCertificationId(user.getId(),
                    certification.getId());

            if (!alreadyOwned) {
                userCertificationRepository.save(new UserCertification(user, certification));
            }
        }

        shoppingCartItemRepository.deleteByUserId(user.getId());
    }

    @Transactional
    public void removeOwnedCertificationsFromCart(Long userId) {
        if (userId == null) {
            return;
        }

        List<ShoppingCartItem> items = shoppingCartItemRepository.findByUserId(userId);
        for (ShoppingCartItem item : items) {
            Certification certification = item.getCertification();
            if (certification == null) {
                shoppingCartItemRepository.deleteById(item.getId());
                continue;
            }

            boolean alreadyOwned = userCertificationRepository.existsByUserIdAndCertificationId(userId,
                    certification.getId());
            if (alreadyOwned) {
                shoppingCartItemRepository.deleteById(item.getId());
            }
        }
    }
}
