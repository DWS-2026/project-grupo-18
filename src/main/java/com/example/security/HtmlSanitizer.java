package com.example.security;

import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

public class HtmlSanitizer {

    private static final PolicyFactory POLICY = Sanitizers.FORMATTING
            .and(Sanitizers.LINKS)
            .and(Sanitizers.BLOCKS);

    public static String sanitize(String input) {

        if (input == null) {
            return null;
        }

        return POLICY.sanitize(input);
    }
}
