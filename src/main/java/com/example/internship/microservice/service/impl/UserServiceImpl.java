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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PaymentCardRepository paymentCardRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto createUser(UserCreateRequestDto request) {
        User user = userMapper.toEntity(request);
        user.setActive(true);
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return userMapper.toDto(user);
    }

    @Override
    public PagedResponse<UserDto> getAllUsers(int page, int size, String name, String surname) {
        Pageable pageable = PageRequest.of(page, size);

        Specification<User> spec = Specification.where((root, query, cb) -> cb.conjunction());

        if (name != null && !name.isEmpty()) {
            spec = spec.and(UserSpecification.hasName(name));
        }
        if (surname != null && !surname.isEmpty()) {
            spec = spec.and(UserSpecification.hasSurname(surname));
        }

        Page<User> userPage = userRepository.findAll(spec, pageable);
        return PagedResponse.of(userPage.map(userMapper::toDto));
    }

    @Override
    @Transactional
    public UserDto updateUser(Long id, UserUpdateRequestDto request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        userMapper.updateEntityFromDto(request, user);
        User updatedUser = userRepository.save(user);
        return userMapper.toDto(updatedUser);
    }

    @Override
    @Transactional
    public UserDto activateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setActive(true);
        User activatedUser = userRepository.save(user);
        return userMapper.toDto(activatedUser);
    }

    @Override
    @Transactional
    public UserDto deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setActive(false);
        User deactivatedUser = userRepository.save(user);
        paymentCardRepository.deactivateAllCardsByUserId(id);
        return userMapper.toDto(deactivatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        userRepository.delete(user);
    }
}