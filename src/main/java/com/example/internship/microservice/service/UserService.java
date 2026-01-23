package com.example.internship.microservice.service;

import com.example.internship.microservice.model.dto.PagedResponse;
import com.example.internship.microservice.model.dto.user.UserCreateRequestDto;
import com.example.internship.microservice.model.dto.user.UserDto;
import com.example.internship.microservice.model.dto.user.UserUpdateRequestDto;

public interface UserService {
    /**
     * Creates a new user.
     * @param request DTO containing user creation details
     * @return created user as DTO
     */
    UserDto createUser(UserCreateRequestDto request);

    /**
     * Retrieves a user by ID.
     * @param id user identifier
     * @return user details as DTO
     */
    UserDto getUserById(Long id);

    /**
     * Retrieves all users with optional name/surname filtering and pagination.
     * @param page page number
     * @param size number of items per page
     * @param name optional filter by first name (case-insensitive partial match)
     * @param surname optional filter by last name (case-insensitive partial match)
     * @return paginated list of users
     */
    PagedResponse<UserDto> getAllUsers(int page, int size, String name, String surname);

    /**
     * Updates an existing user.
     * @param id user identifier to update
     * @param request DTO with updated user data
     * @return updated user as DTO
     */
    UserDto updateUser(Long id, UserUpdateRequestDto request);

    /**
     * Activates user.
     * @param id user identifier
     * @return activated user as DTO
     */
    UserDto activateUser(Long id);

    /**
     * Deactivates user.
     * @param id user identifier
     * @return deactivated user as DTO
     */
    UserDto deactivateUser(Long id);

    /**
     * Deletes a user permanently.
     * @param id user identifier
     */
    void deleteUser(Long id);
}