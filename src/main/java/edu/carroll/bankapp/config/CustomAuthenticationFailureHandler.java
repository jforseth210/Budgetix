package edu.carroll.bankapp.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import java.io.IOException;

public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        super.onAuthenticationFailure(request, response, exception);
        if(exception.getClass().isAssignableFrom(UsernameNotFoundException.class)) {
            response.sendError(1, "Invalid Username");
        } else if (exception.getClass().isAssignableFrom(LockedException.class)) {
            response.sendError(2, "Account is Locked");
        } else if (exception.getClass().isAssignableFrom(BadCredentialsException.class)) {
            response.sendError(3, "Invalid Username or Password");
        }
    }
}
