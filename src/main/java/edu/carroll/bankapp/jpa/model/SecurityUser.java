package edu.carroll.bankapp.jpa.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A wrapper around our SiteUser model that implements the methods that Spring
 * Security expects from the UserDetails interface.
 */
public class SecurityUser implements UserDetails {

    private final SiteUser siteUser;

    public SecurityUser(SiteUser siteUser) {
        this.siteUser = siteUser;
    }

    @Override
    public String getUsername() {
        return siteUser.getUsername();
    }

    @Override
    public String getPassword() {
        return siteUser.getHashedPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("USER"));
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
