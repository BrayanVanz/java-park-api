package com.brayanvanz.park_api.jwt;

import org.springframework.security.core.authority.AuthorityUtils;

import com.brayanvanz.park_api.entities.User;

public class JwtUserDetails extends org.springframework.security.core.userdetails.User {

    private User user;

    public JwtUserDetails(User user) {
        super(user.getUsername(), user.getPassword(), AuthorityUtils.createAuthorityList(user.getRole().name()));
        this.user = user;
    }

    public Long getId() {
        return this.user.getId();
    }

    public String getRole() {
        return this.user.getRole().name();
    }
}
