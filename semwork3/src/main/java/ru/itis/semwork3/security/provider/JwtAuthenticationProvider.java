package ru.itis.semwork3.security.provider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import ru.itis.semwork3.redis.JwtBlacklistService;
import ru.itis.semwork3.security.authentication.JwtAuthentication;
import ru.itis.semwork3.security.details.UserDetailsImpl;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {
    private final JwtBlacklistService blacklistService;

    @Value("${jwt.secret}") private String JWT_SECRET;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(JWT_SECRET)
                    .parseClaimsJws((String) authentication.getCredentials())
                    .getBody();
        } catch (Exception e) {
            throw new AuthenticationCredentialsNotFoundException("Bad token");
        }
        if (blacklistService.exists((String) authentication.getCredentials())) {
            throw new AuthenticationCredentialsNotFoundException("Bad token");
        }
        if ((long) claims.get("expires") < System.currentTimeMillis()) {
            throw new AuthenticationCredentialsNotFoundException("Bad token");
        }
        UserDetails userDetails = new UserDetailsImpl(String.valueOf(claims.get("id", String.class)),
                claims.get("role", String.class));
        if (blacklistService.exists(userDetails.getUsername())) {
            throw new AuthenticationCredentialsNotFoundException("User is banned");
        }
        authentication.setAuthenticated(true);
        ((JwtAuthentication)authentication).setUserDetails(userDetails);
        return authentication;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthentication.class.isAssignableFrom(authentication);
    }
}
