package com.example.internship.microservice.service.impl;

import com.example.internship.microservice.exception.ResourceNotFoundException;
import com.example.internship.microservice.mapper.UserMapper;
import com.example.internship.microservice.model.dto.PagedResponse;
import com.example.internship.microservice.model.dto.user.UserCreateRequestDto;
import com.example.internship.microservice.model.dto.user.UserDto;
import com.example.internship.microservice.model.dto.user.UserUpdateRequestDto;
import com.example.internship.microservice.model.entity.User;
import com.example.internship.microservice.repository.PaymentCardRepository;
import com.example.internship.microservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PaymentCardRepository paymentCardRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDto userDto;
    private UserCreateRequestDto createRequest;
    private UserUpdateRequestDto updateRequest;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("TestName")
                .surname("TestSur")
                .birthDate(LocalDate.now().minusYears(25))
                .email("test@example.com")
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userDto = UserDto.builder()
                .id(1L)
                .name("TestName")
                .surname("TestSur")
                .birthDate(LocalDate.now().minusYears(25))
                .email("test@example.com")
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        createRequest = UserCreateRequestDto.builder()
                .name("TestName")
                .surname("TestSur")
                .birthDate(LocalDate.now().minusYears(25))
                .email("test@example.com")
                .build();

        updateRequest = UserUpdateRequestDto.builder()
                .name("TestNameUpd")
                .surname("TestSurnameUpd")
                .build();
    }

    @Test
    void createUser_ShouldReturnUserDto() {
        when(userMapper.toEntity(any(UserCreateRequestDto.class))).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toDto(any(User.class))).thenReturn(userDto);

        UserDto result = userService.createUser(createRequest);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void getUserById_WhenUserExists_ShouldReturnUserDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDto(any(User.class))).thenReturn(userDto);

        UserDto result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserById_WhenUserNotExists_ShouldThrowResourceNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(1L));
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getAllUsers_ShouldReturnPagedResponse() {
        Page<User> userPage = new PageImpl<>(Collections.singletonList(user));
        when(userRepository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(userPage);
        when(userMapper.toDto(any(User.class))).thenReturn(userDto);

        PagedResponse<UserDto> result = userService.getAllUsers(0, 10, "TestName", "TestSur");

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(userRepository, times(1)).findAll(any(Specification.class), any(PageRequest.class));
    }

    @Test
    void updateUser_WhenUserExists_ShouldReturnUpdatedUserDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDto(any(User.class))).thenReturn(userDto);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.updateUser(1L, updateRequest);

        assertNotNull(result);
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_WhenUserNotExists_ShouldThrowResourceNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(1L, updateRequest));
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void deleteUser_WhenUserExists_ShouldDeleteSuccessfully() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).delete(any(User.class));
    }

    @Test
    void activateUser_WhenUserExists_ShouldReturnActivatedUserDto() {
        User activatedUser = user.toBuilder().active(true).build();
        UserDto activatedUserDto = userDto.toBuilder().active(true).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(activatedUser);
        when(userMapper.toDto(any(User.class))).thenReturn(activatedUserDto);

        UserDto result = userService.activateUser(1L);

        assertNotNull(result);
        assertTrue(result.getActive());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void deactivateUser_WhenUserExists_ShouldReturnDeactivatedUserDto() {
        User deactivatedUser = user.toBuilder().active(false).build();
        UserDto deactivatedUserDto = userDto.toBuilder().active(false).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(deactivatedUser);
        when(userMapper.toDto(any(User.class))).thenReturn(deactivatedUserDto);

        UserDto result = userService.deactivateUser(1L);

        assertNotNull(result);
        assertFalse(result.getActive());
        verify(userRepository, times(1)).save(any(User.class));
        verify(paymentCardRepository, times(1)).deactivateAllCardsByUserId(1L);
    }
}
