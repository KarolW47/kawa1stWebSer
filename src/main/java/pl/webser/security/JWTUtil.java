package pl.webser.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JWTUtil {
    @Value("${jwt.expiration.time}")
    private int jwtExpirationTime;

    @Value("${jwt.secret}")
    private String jwtSecret;

    public Algorithm generateAlgorithmWithPassedSecret(String secret) {
        return Algorithm.HMAC512(secret);
    }

    public String generateJwtToken(Authentication authentication) {

        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();

        return JWT.create()
                .withSubject(userPrincipal.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtExpirationTime))
                .withClaim("roles", userPrincipal.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .sign(generateAlgorithmWithPassedSecret(jwtSecret));
    }

    public JWTVerifier jwtVerifier() {
        return JWT.require(generateAlgorithmWithPassedSecret(jwtSecret)).build();
    }

    public String getUserNameFromJwtToken(String token) {
        return jwtVerifier().verify(token).getSubject();
    }


}
