package pl.webser.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

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

    public String generateJwtToken(String emailAddress, List<String> roles) {

        return JWT.create()
                .withSubject(emailAddress)
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtExpirationTime))
                .withClaim("roles", roles)
                .sign(generateAlgorithmWithPassedSecret(jwtSecret));
    }

    public String generateJwtRefreshToken(String emailAddress) {

        return JWT.create()
                .withSubject(emailAddress)
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtRefreshExpirationTime))
                .sign(generateAlgorithmWithPassedSecret(jwtSecret));
    }

    public DecodedJWT decodeJWT(String token) {
        return JWT.require(generateAlgorithmWithPassedSecret(jwtSecret)).build().verify(token);
    }

    public String getEmailAddressFromJwtToken(String token) {
        return decodeJWT(token).getSubject();
    }

    public Date expirationTimeOfToken(String token) throws TokenExpiredException {
        try {
            return decodeJWT(token).getExpiresAt();
        } catch (TokenExpiredException | JWTDecodeException tokenExpiredException) {
            return null;
        }
    }


}
