package pl.webser.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import pl.webser.model.Role;
import pl.webser.model.User;

import java.util.Collection;
import java.util.Date;
import java.util.List;
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

    public String generateJwtToken(String username, List<String> roles) {

        return JWT.create()
                .withSubject(username)
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtExpirationTime))
                .withClaim("roles", roles.stream().toList())
                .sign(generateAlgorithmWithPassedSecret(jwtSecret));
    }

    public String generateJwtRefreshToken(String username) {

        return JWT.create()
                .withSubject(username)
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtRefreshExpirationTime))
                .sign(generateAlgorithmWithPassedSecret(jwtSecret));
    }

    public DecodedJWT decodeJWT(String token) {
        return JWT.require(generateAlgorithmWithPassedSecret(jwtSecret)).build().verify(token);
    }

    public String getUserNameFromJwtToken(String token) {
        return decodeJWT(token).getSubject();
    }

    public Date expirationTimeOfToken(String token) throws TokenExpiredException {
        try {
            return decodeJWT(token).getExpiresAt();
        } catch (TokenExpiredException tokenExpiredException) {
            return null;
        }
    }


}
