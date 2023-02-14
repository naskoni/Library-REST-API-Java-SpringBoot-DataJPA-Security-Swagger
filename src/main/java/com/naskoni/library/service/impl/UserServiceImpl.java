package com.naskoni.library.service.impl;

import com.google.common.hash.Hashing;
import com.naskoni.library.dao.UserDao;
import com.naskoni.library.dto.UserRequestDto;
import com.naskoni.library.dto.UserResponseDto;
import com.naskoni.library.entity.User;
import com.naskoni.library.enumeration.Status;
import com.naskoni.library.exception.CurrentlyInUseException;
import com.naskoni.library.exception.DuplicateException;
import com.naskoni.library.exception.NotFoundException;
import com.naskoni.library.exception.UserDeactivatedException;
import com.naskoni.library.security.AuthenticationFacade;
import com.naskoni.library.service.UserService;
import com.naskoni.library.specification.SpecificationsBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.regex.Matcher;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  public static final String USER_NOT_FOUND = "User with id: %d could not be found";
  public static final String USER_ALREADY_DEACTIVATED = "User with id: %d is already deactivated";
  public static final String USER_IN_USE =
      "User with id: %d is logged and cannot deactivate himself.";
  public static final String USERNAME_EXIST = "username: %s already exists";

  private final UserDao userDao;
  private final AuthenticationFacade authenticationFacade;

  @Transactional
  @Override
  public UserResponseDto create(UserRequestDto userRequestDto) {
    Optional<User> optionalUser = userDao.findByUsername(userRequestDto.getUsername());
    if (optionalUser.isPresent()) {
      throw new DuplicateException(String.format(USERNAME_EXIST, userRequestDto.getUsername()));
    }

    String enteredPassword = userRequestDto.getPassword();
    String encryptedPassword =
        Hashing.sha256().hashString(enteredPassword, StandardCharsets.UTF_8).toString();
    userRequestDto.setPassword(encryptedPassword);

    User user = mapToEntity(userRequestDto);
    user.setStatus(Status.ACTIVE);
    User savedUser = userDao.save(user);
    return mapToDto(savedUser);
  }

  @Transactional
  @Override
  public UserResponseDto update(Long id, UserRequestDto userRequestDto) {
    Optional<User> optionalUser = userDao.findById(id);
    if (optionalUser.isPresent()) {
      User user = optionalUser.get();
      Optional<User> optionalbyUsername = userDao.findByUsername(userRequestDto.getUsername());
      if (optionalbyUsername.isPresent() && !optionalbyUsername.get().getId().equals(id)) {
        throw new DuplicateException(String.format(USERNAME_EXIST, userRequestDto.getUsername()));
      }

      String enteredPassword = userRequestDto.getPassword();
      String encryptedPassword =
          Hashing.sha256().hashString(enteredPassword, StandardCharsets.UTF_8).toString();
      userRequestDto.setPassword(encryptedPassword);

      BeanUtils.copyProperties(userRequestDto, user);

      User savedUser = userDao.save(user);
      return mapToDto(savedUser);
    } else {
      throw new NotFoundException(String.format(USER_NOT_FOUND, id));
    }
  }

  @Transactional
  @Override
  public UserResponseDto deactivate(Long id) {
    Optional<User> userOptional = userDao.findById(id);
    if (userOptional.isPresent()) {
      User user = userOptional.get();
      if (user.getStatus() == Status.DEACTIVATED) {
        throw new UserDeactivatedException(String.format(USER_ALREADY_DEACTIVATED, id));
      }

      String loggedUsername = authenticationFacade.getAuthentication().getName();
      if (user.getUsername().equals(loggedUsername)) {
        throw new CurrentlyInUseException(String.format(USER_IN_USE, id));
      }

      user.setStatus(Status.DEACTIVATED);
      User savedUser = userDao.save(user);
      return mapToDto(savedUser);
    } else {
      throw new NotFoundException(String.format(USER_NOT_FOUND, id));
    }
  }

  @Transactional(readOnly = true)
  @Override
  public UserResponseDto findOne(Long id) {
    Optional<User> optionalUser = userDao.findById(id);
    if (optionalUser.isPresent()) {
      return mapToDto(optionalUser.get());
    } else {
      throw new NotFoundException(String.format(USER_NOT_FOUND, id));
    }
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
    Page<User> users = userDao.findAll(spec, pageable);
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
