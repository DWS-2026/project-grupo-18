package com.example.cybercert.Services;

import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;

@Service
public class ShoppingCartService {

    private static final String CART_SESSION_KEY = "shoppingCartCertificationIds";

    public Set<Long> getCartIds(HttpSession session) {
        Object value = session.getAttribute(CART_SESSION_KEY);
        if (value instanceof Set<?>) {
            Set<Long> safeIds = new LinkedHashSet<>();
            for (Object item : (Set<?>) value) {
                if (item instanceof Long id) {
                    safeIds.add(id);
                } else if (item instanceof Integer number) {
                    safeIds.add(number.longValue());
                }
            }
            return safeIds;
        }
        return new LinkedHashSet<>();
    }

    public void addToCart(HttpSession session, Long certificationId) {
        Set<Long> cartIds = getCartIds(session);
        cartIds.add(certificationId);
        session.setAttribute(CART_SESSION_KEY, cartIds);
    }

    public void removeFromCart(HttpSession session, Long certificationId) {
        Set<Long> cartIds = getCartIds(session);
        cartIds.remove(certificationId);
        session.setAttribute(CART_SESSION_KEY, cartIds);
    }

    public void clear(HttpSession session) {
        session.setAttribute(CART_SESSION_KEY, new LinkedHashSet<Long>());
    }
}
