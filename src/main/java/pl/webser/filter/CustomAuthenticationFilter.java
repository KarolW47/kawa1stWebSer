package pl.webser.filter;

import com.auth0.jwt.JWT;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.stream.Collectors;

import static pl.webser.filter.FilterUtil.algorithmWithSecret;
import static pl.webser.filter.FilterUtil.expirationTime;

@Slf4j
@AllArgsConstructor
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        log.info("Attempt for - Username: {} and Password: {}", username, password);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username,
                password);
        return authenticationManager.authenticate(authenticationToken);
    }

    // TODO: 01.02.2022 Gotta set lock for user on specified period of time, if there are too many login attempts
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
        super.unsuccessfulAuthentication(request, response, failed);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authentication) throws IOException, ServletException {
        //"User" taken from springframework.security.core.userdetails.User which require username, password and authorities
        User user = (User) authentication.getPrincipal();
        String accessToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(expirationTime())
                //it might be company name or whatever, at this moment im passing url
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles", user.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .sign(algorithmWithSecret);

        String refreshToken = JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationTime))
                .withIssuer(request.getRequestURL().toString())
                .sign(algorithmWithSecret);

        response.setHeader("accessToken", accessToken);
        response.setHeader("refreshToken", refreshToken);
    }
}
