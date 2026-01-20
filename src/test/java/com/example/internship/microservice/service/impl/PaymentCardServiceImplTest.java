package com.example.internship.microservice.service.impl;

import com.example.internship.microservice.exception.CardLimitExceededException;
import com.example.internship.microservice.exception.ResourceNotFoundException;
import com.example.internship.microservice.mapper.PaymentCardMapper;
import com.example.internship.microservice.model.dto.PagedResponse;
import com.example.internship.microservice.model.dto.paymentcard.PaymentCardCreateRequestDto;
import com.example.internship.microservice.model.dto.paymentcard.PaymentCardDto;
import com.example.internship.microservice.model.dto.paymentcard.PaymentCardUpdateRequestDto;
import com.example.internship.microservice.model.entity.PaymentCard;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentCardServiceImplTest {

    @Mock
    private PaymentCardRepository paymentCardRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PaymentCardMapper paymentCardMapper;

    @InjectMocks
    private PaymentCardServiceImpl paymentCardService;

    private PaymentCard card;
    private PaymentCardDto cardDto;
    private PaymentCardCreateRequestDto createRequest;
    private PaymentCardUpdateRequestDto updateRequest;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("TestName")
                .surname("TestSur")
                .build();

        card = PaymentCard.builder()
                .id(1L)
                .user(user)
                .number("1234-5678-9012-3456")
                .holder("TestName TestSur")
                .expirationDate(LocalDate.now().plusYears(5))
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        cardDto = PaymentCardDto.builder()
                .id(1L)
                .userId(1L)
                .number("1234-5678-9012-3456")
                .holder("TestName TestSur")
                .expirationDate(LocalDate.now().plusYears(5))
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        createRequest = PaymentCardCreateRequestDto.builder()
                .userId(1L)
                .number("1234-5678-9012-3456")
                .holder("TestName TestSur")
                .expirationDate(LocalDate.now().plusYears(5))
                .build();

        updateRequest = PaymentCardUpdateRequestDto.builder()
                .holder("FullName Upd")
                .build();
    }

    @Test
    void createCard_WhenUserHasLessThanFiveCards_ShouldReturnCardDto() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(paymentCardRepository.countByUserId(1L)).thenReturn(3L);
        when(paymentCardMapper.toEntity(any(PaymentCardCreateRequestDto.class))).thenReturn(card);
        when(paymentCardRepository.save(any(PaymentCard.class))).thenReturn(card);
        when(paymentCardMapper.toDto(any(PaymentCard.class))).thenReturn(cardDto);

        PaymentCardDto result = paymentCardService.createCard(createRequest);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(paymentCardRepository, times(1)).save(any(PaymentCard.class));
    }

    @Test
    void createCard_WhenUserHasFiveCards_ShouldThrowCardLimitExceededException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(paymentCardRepository.countByUserId(1L)).thenReturn(5L);

        assertThrows(CardLimitExceededException.class, () -> paymentCardService.createCard(createRequest));
        verify(paymentCardRepository, never()).save(any(PaymentCard.class));
    }

    @Test
    void getCardById_WhenCardExists_ShouldReturnCardDto() {
        when(paymentCardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(paymentCardMapper.toDto(any(PaymentCard.class))).thenReturn(cardDto);

        PaymentCardDto result = paymentCardService.getCardById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(paymentCardRepository, times(1)).findById(1L);
    }

    @Test
    void getCardById_WhenCardNotExists_ShouldThrowResourceNotFoundException() {
        when(paymentCardRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> paymentCardService.getCardById(1L));
        verify(paymentCardRepository, times(1)).findById(1L);
    }

    @Test
    void getAllCards_ShouldReturnPagedResponse() {
        Page<PaymentCard> cardPage = new PageImpl<>(Collections.singletonList(card));
        when(paymentCardRepository.findAll(any(PageRequest.class))).thenReturn(cardPage);
        when(paymentCardMapper.toDto(any(PaymentCard.class))).thenReturn(cardDto);

        PagedResponse<PaymentCardDto> result = paymentCardService.getAllCards(0, 10);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(paymentCardRepository, times(1)).findAll(any(PageRequest.class));
    }

    @Test
    void getCardsByUserId_ShouldReturnPagedResponse() {
        Page<PaymentCard> cardPage = new PageImpl<>(Collections.singletonList(card));
        when(paymentCardRepository.findByUserId(eq(1L), any(PageRequest.class))).thenReturn(cardPage);
        when(paymentCardMapper.toDto(any(PaymentCard.class))).thenReturn(cardDto);

        PagedResponse<PaymentCardDto> result = paymentCardService.getCardsByUserId(1L, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(paymentCardRepository, times(1)).findByUserId(eq(1L), any(PageRequest.class));
    }

    @Test
    void updateCard_WhenCardExists_ShouldReturnUpdatedCardDto() {
        when(paymentCardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(paymentCardRepository.save(any(PaymentCard.class))).thenReturn(card);
        when(paymentCardMapper.toDto(any(PaymentCard.class))).thenReturn(cardDto);

        PaymentCardDto result = paymentCardService.updateCard(1L, updateRequest);

        assertNotNull(result);
        verify(paymentCardRepository, times(1)).findById(1L);
        verify(paymentCardRepository, times(1)).save(any(PaymentCard.class));
    }

    @Test
    void updateCard_WhenCardNotExists_ShouldThrowResourceNotFoundException() {
        when(paymentCardRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> paymentCardService.updateCard(1L, updateRequest));
        verify(paymentCardRepository, times(1)).findById(1L);
    }

    @Test
    void activateCard_WhenCardExists_ShouldReturnActivatedCardDto() {
        when(paymentCardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(paymentCardRepository.save(any(PaymentCard.class))).thenReturn(card);
        when(paymentCardMapper.toDto(any(PaymentCard.class))).thenReturn(cardDto);

        PaymentCardDto result = paymentCardService.activateCard(1L);

        assertNotNull(result);
        assertTrue(result.getActive());
        verify(paymentCardRepository, times(1)).save(any(PaymentCard.class));
    }

    @Test
    void deactivateCard_WhenCardExists_ShouldReturnDeactivatedCardDto() {
        PaymentCard deactivatedCard = card.toBuilder().active(false).build();
        PaymentCardDto deactivatedCardDto = cardDto.toBuilder().active(false).build();

        when(paymentCardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(paymentCardRepository.save(any(PaymentCard.class))).thenReturn(deactivatedCard);
        when(paymentCardMapper.toDto(any(PaymentCard.class))).thenReturn(deactivatedCardDto);

        PaymentCardDto result = paymentCardService.deactivateCard(1L);

        assertNotNull(result);
        assertFalse(result.getActive());
        verify(paymentCardRepository, times(1)).save(any(PaymentCard.class));
    }
}