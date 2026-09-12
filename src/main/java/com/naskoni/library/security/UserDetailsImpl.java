package com.naskoni.library.security;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public final class UserDetailsImpl implements UserDetails {

  private static final long serialVersionUID = -5025098630443219650L;

  private final boolean accountNonExpired;
  private final boolean accountNonLocked;
  private final boolean credentialsNonExpired;
  private final boolean enabled;
  private final String username;
  private final String password;
  private final Collection<GrantedAuthority> authorities;

  public UserDetailsImpl(
      String username,
      String password,
      Collection<GrantedAuthority> authorities) {
    this.enabled = true;
    this.accountNonExpired = true;
    this.credentialsNonExpired = true;
    this.accountNonLocked = true;
    this.username = username;
    this.password = password;
    this.authorities = authorities;
  }

  @Override
  public boolean isAccountNonExpired() {
    return accountNonExpired;
  }

  @Override
  public boolean isAccountNonLocked() {
    return accountNonLocked;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return credentialsNonExpired;
  }

  @Override
  public boolean isEnabled() {
    return enabled;
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public Collection<GrantedAuthority> getAuthorities() {
    return authorities;
  }
}