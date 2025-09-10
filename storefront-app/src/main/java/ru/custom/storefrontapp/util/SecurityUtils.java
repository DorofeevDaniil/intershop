package ru.custom.storefrontapp.util;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SecurityUtils {
    public boolean isAnonymous(Authentication auth) {
        return auth == null
                || auth instanceof AnonymousAuthenticationToken
                || auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ANONYMOUS"));
    }

    public Optional<String> getUsername(Authentication auth) {
        if (isAnonymous(auth)) {
            return Optional.empty();
        }
        return Optional.of(auth.getName());
    }
}
