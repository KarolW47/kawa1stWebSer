package pl.webser.security.filter;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.webser.model.User;
import pl.webser.security.JWTUtil;
import pl.webser.service.UserService;

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
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    public CustomAuthorizationFilter(JWTUtil jwtUtil, UserService userService,
                                     AuthenticationManager authenticationManager) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String accessToken = request.getHeader(ACCESS_TOKEN_HEADER);
        String refreshToken = request.getHeader(REFRESH_TOKEN_HEADER);
        if (request.getServletPath().equals("/user/login") || request.getServletPath().equals("/user/register")) {
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
                String regeneratedAccessToken = dealingWithValidRefreshToken(refreshToken);
                log.info("Access granted, generated new access token.");
                response.setHeader(ACCESS_TOKEN_HEADER, regeneratedAccessToken);
                response.setHeader(REFRESH_TOKEN_HEADER, refreshToken);
                filterChain.doFilter(request, response);
            } catch (Exception exception) {
                response.setHeader("Error", exception.getMessage());
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
        String username = jwtUtil.getUserNameFromJwtToken(accessToken);
        String[] roles = jwtUtil.decodeJWT(accessToken).getClaim("roles").asArray(String.class);
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        stream(roles).forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                username, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    private String dealingWithValidRefreshToken(String refreshToken) {
        String username = jwtUtil.getUserNameFromJwtToken(refreshToken);
        User user = userService.getUserByUsername(username);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(),
                        user.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return jwtUtil.generateJwtToken(authentication);
    }

    private boolean isTokenBeforeExpirationTime(String token) {
        Date dateFromToken = jwtUtil.expirationTimeOfToken(token);
        log.info("date from token: {}",dateFromToken);
        if (dateFromToken != null) {
            Date currentDate = new Date(System.currentTimeMillis());
            log.info("current date: {}", currentDate);
            return dateFromToken.after(currentDate);
        } else {
            return false;
        }
    }
}
