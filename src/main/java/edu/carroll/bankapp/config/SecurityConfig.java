package edu.carroll.bankapp.config;

import edu.carroll.bankapp.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
/**
 * Set up Spring Security
 */
public class SecurityConfig {

    private final CustomUserDetailsService myUserDetailsService;

    public SecurityConfig(CustomUserDetailsService myUserDetailsService) {
        this.myUserDetailsService = myUserDetailsService;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(requests -> requests
                        .requestMatchers("/css/**", "/loginNew").permitAll()
                        .requestMatchers("/", "/accounts").permitAll()
                        .anyRequest().authenticated())
                .userDetailsService(myUserDetailsService)
                .formLogin(formLogin -> formLogin.loginPage("/login").permitAll())
                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
