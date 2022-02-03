package pl.webser.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

public class FilterUtil {
    @Value("${jwt.expirationTime}")
    public static int expirationTime;

    public static final Algorithm algorithmWithSecret = Algorithm.HMAC256("${jwt.secret}".getBytes());

    public static DecodedJWT decodeJWT(String token) {
        JWTVerifier jwtVerifier = JWT.require(algorithmWithSecret).build();
        return jwtVerifier.verify(token);
    }

    public static Date expirationTime(){
        return new Date(System.currentTimeMillis() + expirationTime);
    }
}
