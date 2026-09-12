package com.naskoni.library.service.impl;

import com.google.common.hash.Hashing;
import com.naskoni.library.dto.UserRequestDto;
import com.naskoni.library.dto.UserResponseDto;
import com.naskoni.library.entity.User;
import com.naskoni.library.enumeration.Status;
import com.naskoni.library.exception.CurrentlyInUseException;
import com.naskoni.library.exception.DuplicateException;
import com.naskoni.library.exception.NotFoundException;
import com.naskoni.library.exception.UserDeactivatedException;
import com.naskoni.library.repository.UserRepository;
import com.naskoni.library.security.AuthenticationFacade;
import com.naskoni.library.service.UserService;
import com.naskoni.library.specification.SpecificationsBuilder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  public static final String USER_NOT_FOUND = "User with id: %d could not be found";
  public static final String USER_ALREADY_DEACTIVATED = "User with id: %d is already deactivated";
  public static final String USER_IN_USE =
      "User with id: %d is logged and cannot deactivate himself.";
  public static final String USERNAME_EXIST = "username: %s already exists";

  private final UserRepository userRepository;
  private final AuthenticationFacade authenticationFacade;

  @Transactional
  @Override
  public UserResponseDto create(UserRequestDto userRequestDto) {
    var existingUser = userRepository.findByUsername(userRequestDto.getUsername());
    if (existingUser.isPresent()) {
      throw new DuplicateException(
          USERNAME_EXIST.formatted(userRequestDto.getUsername()));
    }

    var encryptedPassword = Hashing.sha256()
        .hashString(userRequestDto.getPassword(), StandardCharsets.UTF_8)
        .toString();
    userRequestDto.setPassword(encryptedPassword);

    var user = mapToEntity(userRequestDto);
    user.setStatus(Status.ACTIVE);

    var savedUser = userRepository.save(user);
    return mapToDto(savedUser);
  }

  @Transactional
  @Override
  public UserResponseDto update(Long id, UserRequestDto userRequestDto) {
    var user = userRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.formatted(id)));

    var existingUser = userRepository.findByUsername(userRequestDto.getUsername());
    if (existingUser.isPresent() && !existingUser.get().getId().equals(id)) {
      throw new DuplicateException(
          USERNAME_EXIST.formatted(userRequestDto.getUsername()));
    }

    var encryptedPassword = Hashing.sha256()
        .hashString(userRequestDto.getPassword(), StandardCharsets.UTF_8)
        .toString();
    userRequestDto.setPassword(encryptedPassword);

    BeanUtils.copyProperties(userRequestDto, user);

    var savedUser = userRepository.save(user);
    return mapToDto(savedUser);
  }

  @Transactional
  @Override
  public UserResponseDto deactivate(Long id) {
    var user = userRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.formatted(id)));

    if (user.getStatus() == Status.DEACTIVATED) {
      throw new UserDeactivatedException(USER_ALREADY_DEACTIVATED.formatted(id));
    }

    var loggedUsername = authenticationFacade.getAuthentication().getName();
    if (user.getUsername().equals(loggedUsername)) {
      throw new CurrentlyInUseException(USER_IN_USE.formatted(id));
    }

    user.setStatus(Status.DEACTIVATED);

    var savedUser = userRepository.save(user);
    return mapToDto(savedUser);
  }

  @Transactional(readOnly = true)
  @Override
  public UserResponseDto findOne(Long id) {
    var user = userRepository.findById(id)
        .orElseThrow(() -> new NotFoundException(USER_NOT_FOUND.formatted(id)));

    return mapToDto(user);
  }

  @Transactional(readOnly = true)
  @Override
  public Page<UserResponseDto> findAll(String search, Pageable pageable) {
    SpecificationsBuilder<User> builder = new SpecificationsBuilder<>();
    Matcher matcher = Helper.getMatcher(search);

    while (matcher.find()) {
      builder.with(matcher.group(1), matcher.group(2), matcher.group(3));
    }

    Specification<User> spec = builder.build();
    Page<User> users = userRepository.findAll(spec, pageable);

    return users.map(this::mapToDto);
  }

  private UserResponseDto mapToDto(User user) {
    var userDto = new UserResponseDto();
    BeanUtils.copyProperties(user, userDto);

    return userDto;
  }

  User mapToEntity(UserRequestDto userRequestDto) {
    var user = new User();
    BeanUtils.copyProperties(userRequestDto, user);
    return user;
  }
}