package com.example.internship.microservice.service.impl;

import com.example.internship.microservice.model.entity.User;
import com.example.internship.microservice.exception.ResourceNotFoundException;
import com.example.internship.microservice.mapper.UserMapper;
import com.example.internship.microservice.model.dto.PagedResponse;
import com.example.internship.microservice.model.dto.user.UserCreateRequestDto;
import com.example.internship.microservice.model.dto.user.UserDto;
import com.example.internship.microservice.model.dto.user.UserUpdateRequestDto;
import com.example.internship.microservice.repository.PaymentCardRepository;
import com.example.internship.microservice.repository.UserRepository;
import com.example.internship.microservice.service.UserService;
import com.example.internship.microservice.specification.UserSpecification;
import com.example.internship.microservice.utils.LogMessages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PaymentCardRepository paymentCardRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto createUser(UserCreateRequestDto request) {
        log.info(LogMessages.METHOD_START, "createUser");

        User user = userMapper.toEntity(request);
        user.setActive(true);
        User savedUser = userRepository.save(user);
        UserDto userDto = userMapper.toDto(savedUser);

        log.info(LogMessages.USER_CREATED, userDto.getId());
        log.info(LogMessages.METHOD_END, "createUser");

        return userDto;
    }

    @Override
    public UserDto getUserById(Long id) {
        log.info(LogMessages.METHOD_START, "getUserById");

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn(LogMessages.USER_NOT_FOUND, id);
                    return new ResourceNotFoundException("User not found with id: " + id);
                });

        UserDto userDto = userMapper.toDto(user);
        log.info(LogMessages.USER_RETRIEVED, userDto.getId());
        log.info(LogMessages.METHOD_END, "getUserById");

        return userDto;
    }

    @Override
    public PagedResponse<UserDto> getAllUsers(int page, int size, String name, String surname) {
        log.info(LogMessages.METHOD_START, "getAllUsers");
        log.info(LogMessages.USER_SEARCH_STARTED, name, surname);

        Pageable pageable = PageRequest.of(page, size);
        Specification<User> spec = Specification.where((root, query, cb) -> cb.conjunction());

        if (name != null && !name.isEmpty()) {
            spec = spec.and(UserSpecification.hasName(name));
        }
        if (surname != null && !surname.isEmpty()) {
            spec = spec.and(UserSpecification.hasSurname(surname));
        }

        Page<User> userPage = userRepository.findAll(spec, pageable);
        PagedResponse<UserDto> response = PagedResponse.of(userPage.map(userMapper::toDto));

        log.info(LogMessages.USERS_RETRIEVED, userPage.getContent().size(), page, size);
        log.info(LogMessages.METHOD_END, "getAllUsers");

        return response;
    }

    @Override
    @Transactional
    public UserDto updateUser(Long id, UserUpdateRequestDto request) {
        log.info(LogMessages.METHOD_START, "updateUser");

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn(LogMessages.USER_NOT_FOUND, id);
                    return new ResourceNotFoundException("User not found with id: " + id);
                });

        userMapper.updateEntityFromDto(request, user);
        User updatedUser = userRepository.save(user);
        UserDto userDto = userMapper.toDto(updatedUser);

        log.info(LogMessages.USER_UPDATED, userDto.getId());
        log.info(LogMessages.METHOD_END, "updateUser");

        return userDto;
    }

    @Override
    @Transactional
    public UserDto activateUser(Long id) {
        log.info(LogMessages.METHOD_START, "activateUser");

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn(LogMessages.USER_NOT_FOUND, id);
                    return new ResourceNotFoundException("User not found with id: " + id);
                });

        user.setActive(true);
        User activatedUser = userRepository.save(user);
        UserDto userDto = userMapper.toDto(activatedUser);

        log.info(LogMessages.USER_ACTIVATED, userDto.getId());
        log.info(LogMessages.METHOD_END, "activateUser");

        return userDto;
    }

    @Override
    @Transactional
    public UserDto deactivateUser(Long id) {
        log.info(LogMessages.METHOD_START, "deactivateUser");

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn(LogMessages.USER_NOT_FOUND, id);
                    return new ResourceNotFoundException("User not found with id: " + id);
                });

        user.setActive(false);
        User deactivatedUser = userRepository.save(user);
        paymentCardRepository.deactivateAllCardsByUserId(id);

        UserDto userDto = userMapper.toDto(deactivatedUser);

        log.info(LogMessages.USER_DEACTIVATED, userDto.getId());
        log.info(LogMessages.METHOD_END, "deactivateUser");

        return userDto;
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        log.info(LogMessages.METHOD_START, "deleteUser");

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn(LogMessages.USER_NOT_FOUND, id);
                    return new ResourceNotFoundException("User not found with id: " + id);
                });

        userRepository.delete(user);

        log.info(LogMessages.USER_DELETED, id);
        log.info(LogMessages.METHOD_END, "deleteUser");
    }
}