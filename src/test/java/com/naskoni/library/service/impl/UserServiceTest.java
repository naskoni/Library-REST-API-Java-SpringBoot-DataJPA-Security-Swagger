package com.naskoni.library.service.impl;

import com.naskoni.library.repository.UserRepository;
import com.naskoni.library.dto.UserResponseDto;
import com.naskoni.library.entity.User;
import com.naskoni.library.enumeration.Status;
import com.naskoni.library.exception.CurrentlyInUseException;
import com.naskoni.library.exception.DuplicateException;
import com.naskoni.library.exception.NotFoundException;
import com.naskoni.library.exception.UserDeactivatedException;
import com.naskoni.library.security.AuthenticationFacade;
import com.naskoni.library.specification.SpecificationsBuilder;
import com.naskoni.library.util.UsersCreator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class UserServiceTest {

  @Mock private UserRepository userRepository;
  @Mock private AuthenticationFacade authenticationFacade;

  @InjectMocks private UserServiceImpl userService;

  @Test
  void createShouldSuccess() {
    var userRequestDto = UsersCreator.getUserRequestDto();
    User user = userService.mapToEntity(userRequestDto);

    when(userRepository.save(any())).thenReturn(user);
    UserResponseDto userResponseDto = userService.create(userRequestDto);

    assertEquals(user.getId(), userResponseDto.getId());
    assertEquals(user.getName(), userResponseDto.getName());
    assertEquals(user.getUsername(), userResponseDto.getUsername());
    assertEquals(user.getStatus(), userResponseDto.getStatus());
    assertEquals(user.getRole(), userResponseDto.getRole());
  }

  @Test
  void createWithWithUsernameInUseByOtherUserShouldThrowDuplicateException() {
    var userRequestDto = UsersCreator.getUserRequestDto();
    when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(new User()));
    assertThrows(DuplicateException.class, () -> userService.create(userRequestDto));
  }

  @Test
  void updateExistentUserShouldSuccess() {
    var userRequestDto = UsersCreator.getUserRequestDto();
    User user = userService.mapToEntity(userRequestDto);

    when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
    when(userRepository.save(any())).thenReturn(user);
    UserResponseDto userResponseDto = userService.update(1L, userRequestDto);

    assertEquals(user.getId(), userResponseDto.getId());
    assertEquals(user.getName(), userResponseDto.getName());
    assertEquals(user.getUsername(), userResponseDto.getUsername());
    assertEquals(user.getStatus(), userResponseDto.getStatus());
    assertEquals(user.getRole(), userResponseDto.getRole());
  }

  @Test
  void updateNonExistentUserShouldThrowNotFoundException() {
    var userRequestDto = UsersCreator.getUserRequestDto();
    assertThrows(NotFoundException.class, () -> userService.update(1L, userRequestDto));
  }

  @Test
  void updateExistentUserWithUsernameInUseByOtherUserShouldThrowDuplicateException() {
    var userRequestDto = UsersCreator.getUserRequestDto();
    User userById = userService.mapToEntity(userRequestDto);
    User userByUsername = userService.mapToEntity(userRequestDto);
    userByUsername.setId(2L);

    when(userRepository.findById(anyLong())).thenReturn(Optional.of(userById));
    when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(userByUsername));
    assertThrows(DuplicateException.class, () -> userService.update(1L, userRequestDto));
  }

  @Test
  void deactivateExistentUserShouldSuccess() {
    var userRequestDto = UsersCreator.getUserRequestDto();
    User user = userService.mapToEntity(userRequestDto);

    when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
    when(userRepository.save(any())).thenReturn(user);
    Authentication authentication = mock(Authentication.class);
    when(authenticationFacade.getAuthentication()).thenReturn(authentication);
    when(authentication.getName()).thenReturn("admin");
    UserResponseDto userResponseDto = userService.deactivate(1L);

    assertEquals(user.getId(), userResponseDto.getId());
    assertEquals(user.getName(), userResponseDto.getName());
    assertEquals(user.getUsername(), userResponseDto.getUsername());
    assertEquals(user.getStatus(), userResponseDto.getStatus());
    assertEquals(user.getRole(), userResponseDto.getRole());
  }

  @Test
  void deactivateNonExistentUserShouldThrowNotFoundException() {
    assertThrows(NotFoundException.class, () -> userService.deactivate(1L));
  }

  @Test
  void deactivateDeactivatedUserShouldThrowNotFoundException() {
    var userRequestDto = UsersCreator.getUserRequestDto();
    User user = userService.mapToEntity(userRequestDto);
    user.setStatus(Status.DEACTIVATED);

    when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
    assertThrows(UserDeactivatedException.class, () -> userService.deactivate(1L));
  }

  @Test
  void deactivateLoggedUserShouldThrowCurrentlyInUseException() {
    var userRequestDto = UsersCreator.getUserRequestDto();
    User user = userService.mapToEntity(userRequestDto);

    Authentication authentication = mock(Authentication.class);
    when(authenticationFacade.getAuthentication()).thenReturn(authentication);
    when(authentication.getName()).thenReturn("user");
    when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
    assertThrows(CurrentlyInUseException.class, () -> userService.deactivate(1L));
  }

  @Test
  void findOneShouldSuccess() {
    var user = UsersCreator.getUser();

    when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
    UserResponseDto userResponseDto = userService.findOne(1L);

    assertEquals(user.getId(), userResponseDto.getId());
    assertEquals(user.getName(), userResponseDto.getName());
    assertEquals(user.getUsername(), userResponseDto.getUsername());
    assertEquals(user.getStatus(), userResponseDto.getStatus());
    assertEquals(user.getRole(), userResponseDto.getRole());
  }

  @Test
  void findOneNonExistentUserShouldThrowNotFoundException() {
    assertThrows(NotFoundException.class, () -> userService.findOne(1L));
  }

  @Test
  void findAllShouldSuccess() {
    List<User> users = UsersCreator.getUsers();
    Page<User> page = new PageImpl<>(users);

    Pageable pageable = Pageable.unpaged();
    SpecificationsBuilder<User> builder = new SpecificationsBuilder<>();
    Specification<User> spec = builder.build();
    Mockito.when(userRepository.findAll(spec, pageable)).thenReturn(page);
    Page<UserResponseDto> userResponseDtos = userService.findAll(null, pageable);
    assertEquals(10, userResponseDtos.getContent().size());

    UserResponseDto userResponseDto = userResponseDtos.iterator().next();
    User user = users.get(0);
    assertEquals(user.getId(), userResponseDto.getId());
    assertEquals(user.getName(), userResponseDto.getName());
    assertEquals(user.getUsername(), userResponseDto.getUsername());
    assertEquals(user.getStatus(), userResponseDto.getStatus());
    assertEquals(user.getRole(), userResponseDto.getRole());
  }
}
