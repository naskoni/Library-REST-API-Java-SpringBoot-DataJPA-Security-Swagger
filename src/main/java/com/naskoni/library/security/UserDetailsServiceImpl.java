package com.naskoni.library.security;

import com.google.common.base.Preconditions;
import com.naskoni.library.dao.UserDao;
import com.naskoni.library.entity.User;
import com.naskoni.library.enumeration.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

  public static final String USER_NOT_FOUND = "User with name: %s could not be found";

  private final UserDao userDao;

  @Transactional(readOnly = true)
  @Override
  public UserDetails loadUserByUsername(final String username) {
    Preconditions.checkNotNull(username);
    Optional<User> userOptional = userDao.findByUsername(username);
    if (!userOptional.isPresent() || userOptional.get().getStatus() == Status.DEACTIVATED) {
      throw new UsernameNotFoundException(String.format(USER_NOT_FOUND, username));
    }

    User user = userOptional.get();
    List<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority(user.getRole().toString()));

    return new UserDetailsImpl(user.getUsername(), user.getPassword(), authorities);
  }
}
