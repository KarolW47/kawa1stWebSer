package pl.webser.security.filter;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.webser.security.JWTUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import static java.util.Arrays.stream;

@Slf4j
public class CustomAuthorizationFilter extends OncePerRequestFilter {

    public static final String ACCESS_TOKEN_HEADER = "access_token";
    public static final String REFRESH_TOKEN_HEADER = "refresh_token";

    private final JWTUtil jwtUtil;

    public CustomAuthorizationFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String accessToken = request.getHeader(ACCESS_TOKEN_HEADER);
        String refreshToken = request.getHeader(REFRESH_TOKEN_HEADER);
        if (request.getServletPath().equals("/user/login")
                || request.getServletPath().equals("/user/register")
                || request.getServletPath().equals("/user/token/refresh")
                || request.getServletPath().equals("/user/reset_password")) {
            filterChain.doFilter(request, response);
        } else if (accessToken == null || refreshToken == null) {
            log.info("Token is missing");
            responseAsForbidden(response);
        } else if (isTokenBeforeExpirationTime(accessToken)) {
            try {
                dealingWithValidAccessToken(accessToken);
                log.info("Access granted.");
                filterChain.doFilter(request, response);
            } catch (Exception exception) {
                log.error("Error: {}", exception.getMessage());
                response.setHeader("error", exception.getMessage());
                responseAsForbidden(response);
            }
        } else if (isTokenBeforeExpirationTime(refreshToken)) {
            try {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
            } catch (Exception exception) {
                log.info("Error {}", exception.getMessage());
                response.setHeader("error", exception.getMessage());
                responseAsForbidden(response);
            }
        } else {
            log.info("Something went wrong.");
            responseAsForbidden(response);
        }
    }

    private void responseAsForbidden(HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.sendError(HttpStatus.FORBIDDEN.value());
    }

    private void dealingWithValidAccessToken(String accessToken) {
        String emailAddress = jwtUtil.getEmailAddressFromJwtToken(accessToken);
        String[] roles = jwtUtil.decodeJWT(accessToken).getClaim("roles").asArray(String.class);
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        stream(roles).forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                emailAddress, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    private boolean isTokenBeforeExpirationTime(String token) {
        Date dateFromToken = jwtUtil.expirationTimeOfToken(token);
        log.info("date from token: {}", dateFromToken);
        if (dateFromToken != null) {
            Date currentDate = new Date(System.currentTimeMillis());
            log.info("current date: {}", currentDate);
            return dateFromToken.after(currentDate);
        } else {
            return false;
        }
    }
}
