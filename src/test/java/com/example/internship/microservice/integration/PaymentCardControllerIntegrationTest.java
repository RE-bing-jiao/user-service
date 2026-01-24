package com.example.internship.microservice.integration;

import com.example.internship.microservice.model.dto.ApiResponse;
import com.example.internship.microservice.model.dto.PagedResponse;
import com.example.internship.microservice.model.entity.User;
import com.example.internship.microservice.repository.PaymentCardRepository;
import com.example.internship.microservice.repository.UserRepository;
import com.redis.testcontainers.RedisContainer;
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
import com.example.internship.microservice.model.dto.paymentcard.PaymentCardCreateRequestDto;
import com.example.internship.microservice.model.dto.paymentcard.PaymentCardDto;
import com.example.internship.microservice.model.dto.paymentcard.PaymentCardUpdateRequestDto;
import com.example.internship.microservice.model.entity.PaymentCard;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PaymentCardControllerIntegrationTest {

    @Container
    @SuppressWarnings("resource")
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            DockerImageName.parse("postgres:15"))
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Container
    static RedisContainer redis = new RedisContainer(
            DockerImageName.parse("redis:7-alpine"));

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () ->
                "jdbc:tc:postgresql:15:///testdb?TC_DAEMON=true");
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () ->
                "org.testcontainers.jdbc.ContainerDatabaseDriver");

        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", redis::getFirstMappedPort);
        registry.add("spring.cache.type", () -> "redis");
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentCardRepository paymentCardRepository;

    private final String baseUrl = "/api/v1/payment-cards";
    private User user;

    @BeforeEach
    void setUp() {
        paymentCardRepository.deleteAll();
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
    void createCard_WhenUserExistsAndUnderLimit_ShouldReturnCreatedCard() {
        User savedUser = userRepository.save(user);

        PaymentCardCreateRequestDto request = PaymentCardCreateRequestDto.builder()
                .userId(savedUser.getId())
                .number("1234-5678-9012-3456")
                .holder("TestName TestSur")
                .expirationDate(LocalDate.now().plusYears(5))
                .build();

        ParameterizedTypeReference<ApiResponse<PaymentCardDto>> responseType =
                new ParameterizedTypeReference<>() {
                };

        ResponseEntity<ApiResponse<PaymentCardDto>> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.POST,
                new HttpEntity<>(request),
                responseType);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertNotNull(response.getBody().getData());
        assertEquals("TestName TestSur", response.getBody().getData().getHolder());
    }

    @Test
    void getCardById_WhenCardExists_ShouldReturnCard() {
        User savedUser = userRepository.save(user);

        PaymentCard card = PaymentCard.builder()
                .user(savedUser)
                .number("1234-5678-9012-3456")
                .holder("TestName TestSur")
                .expirationDate(LocalDate.now().plusYears(5))
                .active(true)
                .build();
        PaymentCard savedCard = paymentCardRepository.save(card);

        ParameterizedTypeReference<ApiResponse<PaymentCardDto>> responseType =
                new ParameterizedTypeReference<>() {
                };

        ResponseEntity<ApiResponse<PaymentCardDto>> response = restTemplate.exchange(
                baseUrl + "/" + savedCard.getId(),
                HttpMethod.GET,
                null,
                responseType);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("TestName TestSur", response.getBody().getData().getHolder());
    }

    @Test
    void updateCard_WhenCardExists_ShouldReturnUpdatedCard() {

        User savedUser = userRepository.save(user);

        PaymentCard card = PaymentCard.builder()
                .user(savedUser)
                .number("1234-5678-9012-3456")
                .holder("TestName TestSur")
                .expirationDate(LocalDate.now().plusYears(5))
                .active(true)
                .build();
        PaymentCard savedCard = paymentCardRepository.save(card);

        PaymentCardUpdateRequestDto updateRequest = PaymentCardUpdateRequestDto.builder()
                .holder("FullName Upd")
                .build();

        ParameterizedTypeReference<ApiResponse<PaymentCardDto>> responseType =
                new ParameterizedTypeReference<>() {
                };

        HttpEntity<PaymentCardUpdateRequestDto> requestEntity = new HttpEntity<>(updateRequest);
        ResponseEntity<ApiResponse<PaymentCardDto>> response = restTemplate.exchange(
                baseUrl + "/" + savedCard.getId(),
                HttpMethod.PUT,
                requestEntity,
                responseType);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertEquals("FullName Upd", response.getBody().getData().getHolder());
    }

    @Test
    void deleteCard_WhenCardExists_ShouldReturnSuccess() {
        User savedUser = userRepository.save(user);

        PaymentCard card = PaymentCard.builder()
                .user(savedUser)
                .number("1234-5678-9012-3456")
                .holder("TestName TestSur")
                .expirationDate(LocalDate.now().plusYears(5))
                .active(true)
                .build();
        PaymentCard savedCard = paymentCardRepository.save(card);

        ParameterizedTypeReference<ApiResponse<Void>> responseType =
                new ParameterizedTypeReference<>() {
                };

        ResponseEntity<ApiResponse<Void>> response = restTemplate.exchange(
                baseUrl + "/" + savedCard.getId(),
                HttpMethod.DELETE,
                null,
                responseType);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());

        Optional<PaymentCard> deletedCard = paymentCardRepository.findById(savedCard.getId());
        assertTrue(deletedCard.isEmpty());
    }

    @Test
    void activateCard_WhenCardExists_ShouldReturnActivatedCard() {
        User savedUser = userRepository.save(user);

        PaymentCard card = PaymentCard.builder()
                .user(savedUser)
                .number("1234-5678-9012-3456")
                .holder("TestName TestSur")
                .expirationDate(LocalDate.now().plusYears(5))
                .active(false)
                .build();
        PaymentCard savedCard = paymentCardRepository.save(card);

        ParameterizedTypeReference<ApiResponse<PaymentCardDto>> responseType =
                new ParameterizedTypeReference<>() {
                };

        ResponseEntity<ApiResponse<PaymentCardDto>> response = restTemplate.exchange(
                baseUrl + "/" + savedCard.getId() + "/activate",
                HttpMethod.PUT,
                new HttpEntity<>("{}"),
                responseType);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertTrue(response.getBody().getData().getActive());
    }

    @Test
    void deactivateCard_WhenCardExists_ShouldReturnDeactivatedCard() {
        User savedUser = userRepository.save(user);

        PaymentCard card = PaymentCard.builder()
                .user(savedUser)
                .number("1234-5678-9012-3456")
                .holder("TestName TestSur")
                .expirationDate(LocalDate.now().plusYears(5))
                .active(true)
                .build();
        PaymentCard savedCard = paymentCardRepository.save(card);

        ParameterizedTypeReference<ApiResponse<PaymentCardDto>> responseType =
                new ParameterizedTypeReference<>() {
                };

        ResponseEntity<ApiResponse<PaymentCardDto>> response = restTemplate.exchange(
                baseUrl + "/" + savedCard.getId() + "/deactivate",
                HttpMethod.PUT,
                new HttpEntity<>("{}"),
                responseType);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertFalse(response.getBody().getData().getActive());
    }

    @Test
    void getCardsByUserId_WhenUserExists_ShouldReturnCards() {
        User savedUser = userRepository.save(user);

        PaymentCard card = PaymentCard.builder()
                .user(savedUser)
                .number("1234-5678-9012-3456")
                .holder("TestName TestSur")
                .expirationDate(LocalDate.now().plusYears(5))
                .active(true)
                .build();
        paymentCardRepository.save(card);

        ParameterizedTypeReference<ApiResponse<PagedResponse<PaymentCardDto>>> responseType =
                new ParameterizedTypeReference<>() {
                };

        ResponseEntity<ApiResponse<PagedResponse<PaymentCardDto>>> response = restTemplate.exchange(
                baseUrl + "/user/" + savedUser.getId() + "?page=0&size=10",
                HttpMethod.GET,
                null,
                responseType);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isSuccess());
        assertNotNull(response.getBody().getData());
        assertFalse(response.getBody().getData().getContent().isEmpty());
        assertEquals(savedUser.getId(), response.getBody().getData().getContent().getFirst().getUserId());
    }

    @Test
    void getAllCards_ShouldReturnPagedResponse() {
        User savedUser = userRepository.save(user);

        PaymentCard card = PaymentCard.builder()
                .user(savedUser)
                .number("1234-5678-9012-3456")
                .holder("TestName TestSur")
                .expirationDate(LocalDate.now().plusYears(5))
                .active(true)
                .build();
        paymentCardRepository.save(card);

        ParameterizedTypeReference<ApiResponse<PagedResponse<PaymentCardDto>>> responseType =
                new ParameterizedTypeReference<>() {
                };

        ResponseEntity<ApiResponse<PagedResponse<PaymentCardDto>>> response = restTemplate.exchange(
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
