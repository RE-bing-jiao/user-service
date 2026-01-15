package com.example.internship.microservice.service;

import com.example.internship.microservice.model.dto.PagedResponse;
import com.example.internship.microservice.model.dto.user.UserCreateRequestDto;
import com.example.internship.microservice.model.dto.user.UserDto;
import com.example.internship.microservice.model.dto.user.UserUpdateRequestDto;

public interface UserService {

    UserDto createUser(UserCreateRequestDto request);

    UserDto getUserById(Long id);

    PagedResponse<UserDto> getAllUsers(int page, int size, String name, String surname);

    UserDto updateUser(Long id, UserUpdateRequestDto request);

    UserDto activateUser(Long id);

    UserDto deactivateUser(Long id);

    void deleteUser(Long id);
}