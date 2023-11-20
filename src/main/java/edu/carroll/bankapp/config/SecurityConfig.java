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

/**
 * Setup Spring Security
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailsService myUserDetailsService;

    /**
     * Counstructor with dependency injection
     * 
     * @param myUserDetailsService - service for looking up persisted users
     */
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
                // Don't redirect to "/logout" after signing back in
                .logout(logout -> logout.logoutSuccessUrl("/"))
                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
