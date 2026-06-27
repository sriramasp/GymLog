package com.gymlog.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class CustomUserDetails extends User {

    private final Long id;
    private final String role;

    public CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities, Long id, String role) {
        super(username, password, authorities);
        this.id = id;
        this.role = role;
    }
}
