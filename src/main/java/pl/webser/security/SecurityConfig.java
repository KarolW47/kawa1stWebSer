package pl.webser.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
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

    private final JWTUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    @Autowired
    public SecurityConfig(JWTUtil jwtUtil,
                          UserDetailsService userDetailsService,
                          PasswordEncoder passwordEncoder,
                          UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    @Bean
    public CustomAuthorizationFilter customAuthorizationFilter() throws Exception {
        return new CustomAuthorizationFilter(jwtUtil);
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

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        final String userRole = "ROLE_USER";
        final String moderatorRole = "ROLE_MODERATOR";
        final String adminRole = "ROLE_ADMIN";

        http.cors();
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        //----- order of these matchers matters -----
        http.authorizeRequests().antMatchers(HttpMethod.GET).hasAuthority(userRole);
        http.authorizeRequests().antMatchers(HttpMethod.POST).hasAuthority(userRole);
        http.authorizeRequests().antMatchers(HttpMethod.PATCH).hasAuthority(userRole);
        http.authorizeRequests().antMatchers(HttpMethod.PUT).hasAuthority(userRole);
        http.authorizeRequests().antMatchers(HttpMethod.DELETE).hasAuthority(userRole);
        //-------------------------------------------

        //        http.authorizeRequests().antMatchers(HttpMethod.POST, "/user/lock").hasAuthority(RoleEnum.ROLE_ADMIN
        //        .toString());

        http.authorizeRequests().anyRequest().authenticated();
        http.addFilter(customAuthenticationFilter());
        http.addFilterBefore(customAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.addAllowedOrigin("http://localhost:4200");
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