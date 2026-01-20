package com.example.internship.microservice.integration;

import com.example.internship.microservice.model.dto.ApiResponse;
import com.example.internship.microservice.model.dto.PagedResponse;
import com.example.internship.microservice.model.dto.user.UserCreateRequestDto;
import com.example.internship.microservice.model.dto.user.UserDto;
import com.example.internship.microservice.model.dto.user.UserUpdateRequestDto;
import com.example.internship.microservice.model.entity.User;
import com.example.internship.microservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerIntegrationTest {

    @Container
    @SuppressWarnings("resource")
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () ->
                "jdbc:tc:postgresql:15:///testdb?TC_DAEMON=true");
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () ->
                "org.testcontainers.jdbc.ContainerDatabaseDriver");
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    private final String baseUrl = "/api/v1/users";
    private User user;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        user = User.builder()
                .name("TestName")
                .surname("TestSur")
                .birthDate(LocalDate.now().minusYears(30))
                .email("test@example.com")
                .active(true)
                .build();
    }

    @Test
    void createUser_ShouldReturnCreatedUser() {
        UserCreateRequestDto request = UserCreateRequestDto.builder()
                .name("TestName")
                .surname("TestSur")
                .birthDate(LocalDate.now().minusYears(25))
                .email("test@example.com")
                .build();

        ParameterizedTypeReference<ApiResponse<UserDto>> responseType =
                new ParameterizedTypeReference<>() {
                };

        ResponseEntity<ApiResponse<UserDto>> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.POST,
                new HttpEntity<>(request),
                responseType);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertNotNull(response.getBody().getData());
        assertEquals("TestName", response.getBody().getData().getName());
    }

    @Test
    void getUserById_WhenUserExists_ShouldReturnUser() {
        User savedUser = userRepository.save(user);

        ParameterizedTypeReference<ApiResponse<UserDto>> responseType =
                new ParameterizedTypeReference<>() {
                };

        ResponseEntity<ApiResponse<UserDto>> response = restTemplate.exchange(
                baseUrl + "/" + savedUser.getId(),
                HttpMethod.GET,
                null,
                responseType);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("TestName", response.getBody().getData().getName());
    }

    @Test
    void updateUser_WhenUserExists_ShouldReturnUpdatedUser() {
        User savedUser = userRepository.save(user);

        UserUpdateRequestDto updateRequest = UserUpdateRequestDto.builder()
                .name("Updated")
                .surname("Name")
                .build();

        ParameterizedTypeReference<ApiResponse<UserDto>> responseType = new ParameterizedTypeReference<>() {
        };

        HttpEntity<UserUpdateRequestDto> requestEntity = new HttpEntity<>(updateRequest);
        ResponseEntity<ApiResponse<UserDto>> response = restTemplate.exchange(
                baseUrl + "/" + savedUser.getId(),
                HttpMethod.PUT,
                requestEntity,
                responseType);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("Updated", response.getBody().getData().getName());
    }


    @Test
    void deleteUser_whenUserExists_ShouldReturnSuccess() {
        User savedUser = userRepository.save(user);

        ParameterizedTypeReference<ApiResponse<Void>> responseType = new ParameterizedTypeReference<>() {
        };

        ResponseEntity<ApiResponse<Void>> response = restTemplate.exchange(
                baseUrl + "/" + savedUser.getId(),
                HttpMethod.DELETE,
                null,
                responseType);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());

        Optional<User> deletedUser = userRepository.findById(savedUser.getId());
        assertTrue(deletedUser.isEmpty());
    }

    @Test
    void activateUser_whenUserExists_ShouldReturnActivatedUser() {
        User savedUser = userRepository.save(user.toBuilder().active(false).build());

        ParameterizedTypeReference<ApiResponse<UserDto>> responseType = new ParameterizedTypeReference<>() {
        };

        ResponseEntity<ApiResponse<UserDto>> response = restTemplate.exchange(
                baseUrl + "/" + savedUser.getId() + "/activate",
                HttpMethod.PUT,
                null,
                responseType);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertTrue(response.getBody().getData().getActive());
    }

    @Test
    void deactivateUser_whenUserExists_ShouldReturnDeactivatedUser() {
        User savedUser = userRepository.save(user);

        ParameterizedTypeReference<ApiResponse<UserDto>> responseType = new ParameterizedTypeReference<>() {
        };

        ResponseEntity<ApiResponse<UserDto>> response = restTemplate.exchange(
                baseUrl + "/" + savedUser.getId() + "/deactivate",
                HttpMethod.PUT,
                null,
                responseType);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertFalse(response.getBody().getData().getActive());
    }

    @Test
    void getAllUsers_ShouldReturnPagedResponse() {
        userRepository.save(user);

        ParameterizedTypeReference<ApiResponse<PagedResponse<UserDto>>> responseType =
                new ParameterizedTypeReference<>() {
                };

        ResponseEntity<ApiResponse<PagedResponse<UserDto>>> response = restTemplate.exchange(
                baseUrl + "?page=0&size=10",
                HttpMethod.GET,
                null,
                responseType);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertNotNull(response.getBody().getData());
        assertFalse(response.getBody().getData().getContent().isEmpty());
    }
}
