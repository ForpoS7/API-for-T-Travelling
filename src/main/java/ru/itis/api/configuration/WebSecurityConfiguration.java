package ru.itis.api.configuration;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.itis.api.security.filter.LoginAuthenticationFilter;
import ru.itis.api.security.filter.TokenAuthenticationFilter;
import ru.itis.api.security.filter.UpdateTokensFilter;
import ru.itis.api.security.matcher.SkipPathRequestMatcher;
import ru.itis.api.util.JwtUtil;

import java.util.List;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {

    @Bean
    @Order(Integer.MIN_VALUE)
    public SecurityFilterChain apiSecurityFilterChain(
            HttpSecurity http,
            LoginAuthenticationFilter loginAuthenticationFilter,
            TokenAuthenticationFilter tokenAuthenticationFilter,
            UpdateTokensFilter updateTokensFilter)
            throws Exception {
        HttpSecurity httpSecurity = http.securityMatcher("/api/**")
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/v1/registration",
                                "/swagger-ui/**",
                                "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(AbstractHttpConfigurer::disable)
                .addFilterAt(
                        loginAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterAt(
                        tokenAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(
                        updateTokensFilter,
                        TokenAuthenticationFilter.class);
        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public LoginAuthenticationFilter loginAuthenticationFilter(
            AuthenticationManager authenticationManager,
            @Qualifier("loginAuthenticationSuccessHandler")
            AuthenticationSuccessHandler successHandler,
            @Qualifier("loginAuthenticationFailureHandler")
            AuthenticationFailureHandler failureHandler) {
        return new LoginAuthenticationFilter(
                "/api/v1/login",
                authenticationManager,
                successHandler,
                failureHandler);
    }

    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter(
            JwtUtil jwtUtil,
            AuthenticationManager authenticationManager,
            AuthenticationFailureHandler failureHandler) {
        SkipPathRequestMatcher requestMatcher = new SkipPathRequestMatcher(
                "/api/v1/login",
                "/api/v1/refresh",
                "/api/v1/registration",
                "/swagger-ui/**",
                "/v3/api-docs/**");
        return new TokenAuthenticationFilter(requestMatcher, jwtUtil, authenticationManager, failureHandler);
    }

    @Bean
    public UpdateTokensFilter updateTokensFilter(
            AuthenticationManager authenticationManager,
            @Qualifier("loginAuthenticationSuccessHandler")
            AuthenticationSuccessHandler successHandler,
            AuthenticationFailureHandler failureHandler) {
        return new UpdateTokensFilter(
                "/api/v1/refresh",
                authenticationManager,
                successHandler,
                failureHandler);
    }

    @Bean
    public AuthenticationManager providerManager(
            List<AuthenticationProvider> providers) {
        return new ProviderManager(providers);
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(
            PasswordEncoder passwordEncoder,
            UserDetailsService userDetailsService) {
        var provider = new DaoAuthenticationProvider(passwordEncoder);
        provider.setUserDetailsService(userDetailsService);
        return provider;
    }

    @Bean
    public JWTVerifier jwtVerifier(Algorithm algorithm) {
        return JWT.require(algorithm).build();
    }

    @Bean
    public Algorithm algorithm(@Value("${jwt.secret}") String secret) {
        return Algorithm.HMAC256(secret);
    }
}
