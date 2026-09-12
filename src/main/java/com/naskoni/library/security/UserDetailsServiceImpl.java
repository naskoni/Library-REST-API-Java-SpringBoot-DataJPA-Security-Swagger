package com.naskoni.library.security;

import com.google.common.base.Preconditions;
import com.naskoni.library.enumeration.Status;
import com.naskoni.library.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

  public static final String USER_NOT_FOUND = "User with name: %s could not be found";

  private final UserRepository userRepository;

  @Transactional(readOnly = true)
  @Override
  public UserDetails loadUserByUsername(final String username) {
    Preconditions.checkNotNull(username);

    var user = userRepository.findByUsername(username)
        .filter(foundUser -> foundUser.getStatus() != Status.DEACTIVATED)
        .orElseThrow(() ->
            new UsernameNotFoundException(USER_NOT_FOUND.formatted(username)));

    List<GrantedAuthority> authorities =
        List.of(new SimpleGrantedAuthority(user.getRole().toString()));

    return new UserDetailsImpl(
        user.getUsername(),
        user.getPassword(),
        authorities);
  }
}