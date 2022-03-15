package pl.webser.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
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
    private long jwtExpirationTime;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.refresh.expiration.time}")
    private long jwtRefreshExpirationTime;

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

    public String generateJwtRefreshToken(Authentication authentication){

        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();

        return JWT.create()
                .withSubject(userPrincipal.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtRefreshExpirationTime))
                .sign(generateAlgorithmWithPassedSecret(jwtSecret));
    }

    public DecodedJWT decodeJWT(String token) {
        return JWT.require(generateAlgorithmWithPassedSecret(jwtSecret)).build().verify(token);
    }

    public String getUserNameFromJwtToken(String token) {
        return decodeJWT(token).getSubject();
    }

    public Date expirationTimeOfToken(String token){
        return decodeJWT(token).getExpiresAt();
    }


}
