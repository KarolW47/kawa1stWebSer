package pl.webser.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import pl.webser.security.filter.CustomAuthenticationFilter;
import pl.webser.security.filter.CustomAuthorizationFilter;
import pl.webser.service.UserService;

import static pl.webser.security.filter.CustomAuthenticationFilter.USER_ID;
import static pl.webser.security.filter.CustomAuthorizationFilter.ACCESS_TOKEN_HEADER;
import static pl.webser.security.filter.CustomAuthorizationFilter.REFRESH_TOKEN_HEADER;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_MODERATOR = "ROLE_MODERATOR";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    @Value("${security.origin}")
    public static String SECURITY_ORIGIN;

    private final JWTUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    @Autowired
    public SecurityConfig(JWTUtil jwtUtil,
                          PasswordEncoder passwordEncoder,
                          UserService userService) {
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    public CustomAuthenticationFilter customAuthenticationFilter() throws Exception {
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(jwtUtil,
                authenticationManagerBean(), userService);
        customAuthenticationFilter.setFilterProcessesUrl("/user/login");
        return customAuthenticationFilter;
    }

    public CustomAuthorizationFilter customAuthorizationFilter() {
        return new CustomAuthorizationFilter(jwtUtil, userService);
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.cors();
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        //----- order of these matchers matters -----
        http.authorizeRequests().antMatchers(
                "/user/login",
                "/user/register",
                "/user/token/refresh",
                "/user/reset_password", "/chat/**", "/chat_messages/**").permitAll();
        http.authorizeRequests().anyRequest().hasAuthority(ROLE_USER);
        //-------------------------------------------
        http.addFilter(customAuthenticationFilter());
        http.addFilterBefore(customAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    protected CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.addAllowedOrigin(SECURITY_ORIGIN);
        config.setAllowCredentials(true);
        config.addAllowedHeader(ACCESS_TOKEN_HEADER);
        config.addAllowedHeader(REFRESH_TOKEN_HEADER);
        config.addExposedHeader(ACCESS_TOKEN_HEADER);
        config.addExposedHeader(REFRESH_TOKEN_HEADER);
        config.addExposedHeader(USER_ID);
        config.addAllowedHeader(USER_ID);
        source.registerCorsConfiguration("/**", config);
        return source;
    }


}