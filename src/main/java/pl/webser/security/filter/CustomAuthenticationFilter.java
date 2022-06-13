package pl.webser.security.filter;


import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import pl.webser.model.User;
import pl.webser.security.JWTUtil;
import pl.webser.service.UserService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static pl.webser.security.filter.CustomAuthorizationFilter.*;

@Slf4j
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    public static final String USER_ID = "user_id";

    private final JWTUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    public CustomAuthenticationFilter(JWTUtil jwtUtil ,AuthenticationManager authenticationManager, UserService userService){
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        String login = request.getParameter("login");
        String password = request.getParameter("password");
        String username = null;
        if (login.contains("@")){
            try {
                username = userService.getUserByEmailAddress(login).getUsername();
            } catch (NullPointerException exception){
                log.info("Provided email address: {} not found", login);
            }
        } else {
            username = login;
        }

        log.info("Attempt for - Username: {} and Password: {}", username, password);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username,
                password);
        return authenticationManager.authenticate(authenticationToken);
    }

    // TODO: 01.02.2022 Gotta set lock for user on specified period of time, if there are too many login attempts
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response,
                                              AuthenticationException authenticationException) throws IOException,
            ServletException {
        log.error("Unauthorized error: {}", authenticationException.getMessage());
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Unauthorized");
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authentication) throws IOException, ServletException {

        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        List<String> roles = userPrincipal.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        User userFromDb = userService.getUserByUsername(userPrincipal.getUsername());

        String accessToken = jwtUtil.generateJwtToken(userFromDb.getEmailAddress(), roles);
        String refreshToken = jwtUtil.generateJwtRefreshToken(userFromDb.getEmailAddress());
        response.setHeader(ACCESS_TOKEN_HEADER, accessToken);
        response.setHeader(REFRESH_TOKEN_HEADER, refreshToken);
        response.setHeader(USER_ID, String.valueOf(userFromDb.getId()));
        log.info("User: {} logged in successfully", authentication.getName());
    }

}
