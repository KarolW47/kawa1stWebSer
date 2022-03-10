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

import static java.util.Arrays.stream;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
public class CustomAuthorizationFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    public CustomAuthorizationFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if (request.getRequestURL().equals("/user/login") || request.getRequestURL().equals("/user/refreshToken")) {
            filterChain.doFilter(request, response);
        } else {
            String token = request.getHeader(AUTHORIZATION);
            if (token != null ) {
                try {
                    String username = jwtUtil.getUserNameFromJwtToken(token);
                    String[] roles = jwtUtil.jwtVerifier().verify(token).getClaim("roles").asArray(String.class);
                    Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
                    stream(roles).forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));

                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            username, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    filterChain.doFilter(request, response);
                } catch (Exception exception) {
                    log.error("Error: {}", exception.getMessage());
                    response.setHeader("error", exception.getMessage());
                    response.sendError(HttpStatus.FORBIDDEN.value());

                }
            } else {
                log.info("Token is missing");
                filterChain.doFilter(request, response);
            }
        }
    }
}
