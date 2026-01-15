package com.example.internship.microservice.service.impl;

import com.example.internship.microservice.model.entity.PaymentCard;
import com.example.internship.microservice.model.entity.User;
import com.example.internship.microservice.exception.CardLimitExceededException;
import com.example.internship.microservice.exception.ResourceNotFoundException;
import com.example.internship.microservice.mapper.PaymentCardMapper;
import com.example.internship.microservice.model.dto.PagedResponse;
import com.example.internship.microservice.model.dto.paymentcard.PaymentCardCreateRequestDto;
import com.example.internship.microservice.model.dto.paymentcard.PaymentCardDto;
import com.example.internship.microservice.model.dto.paymentcard.PaymentCardUpdateRequestDto;
import com.example.internship.microservice.repository.PaymentCardRepository;
import com.example.internship.microservice.repository.UserRepository;
import com.example.internship.microservice.service.PaymentCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentCardServiceImpl implements PaymentCardService {

    private final PaymentCardRepository paymentCardRepository;
    private final UserRepository userRepository;
    private final PaymentCardMapper paymentCardMapper;

    @Override
    public PaymentCardDto createCard(PaymentCardCreateRequestDto request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        long cardCount = paymentCardRepository.countByUserId(request.getUserId());
        if (cardCount >= 5) {
            throw new CardLimitExceededException("User cannot have more than 5 payment cards");
        }

        PaymentCard card = paymentCardMapper.toEntity(request);
        card.setUser(user);
        card.setActive(true);
        PaymentCard savedCard = paymentCardRepository.save(card);
        return paymentCardMapper.toDto(savedCard);
    }

    @Override
    public PaymentCardDto getCardById(Long id) {
        PaymentCard card = paymentCardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment card not found with id: " + id));
        return paymentCardMapper.toDto(card);
    }

    @Override
    public PagedResponse<PaymentCardDto> getAllCards(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PaymentCard> cardPage = paymentCardRepository.findAll(pageable);
        return PagedResponse.of(cardPage.map(paymentCardMapper::toDto));
    }

    @Override
    public PagedResponse<PaymentCardDto> getCardsByUserId(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PaymentCard> cardPage = paymentCardRepository.findByUserId(userId, pageable);
        return PagedResponse.of(cardPage.map(paymentCardMapper::toDto));
    }

    @Override
    @Transactional
    public PaymentCardDto updateCard(Long id, PaymentCardUpdateRequestDto request) {
        PaymentCard card = paymentCardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment card not found with id: " + id));
        paymentCardMapper.updateEntityFromDto(request, card);
        PaymentCard updatedCard = paymentCardRepository.save(card);
        return paymentCardMapper.toDto(updatedCard);
    }

    @Override
    @Transactional
    public PaymentCardDto activateCard(Long id) {
        PaymentCard card = paymentCardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment card not found with id: " + id));
        card.setActive(true);
        PaymentCard activatedCard = paymentCardRepository.save(card);
        return paymentCardMapper.toDto(activatedCard);
    }

    @Override
    @Transactional
    public PaymentCardDto deactivateCard(Long id) {
        PaymentCard card = paymentCardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment card not found with id: " + id));
        card.setActive(false);
        PaymentCard deactivatedCard = paymentCardRepository.save(card);
        return paymentCardMapper.toDto(deactivatedCard);
    }

    @Override
    @Transactional
    public void deleteCard(Long id) {
        PaymentCard card = paymentCardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment card not found with id: " + id));
        paymentCardRepository.delete(card);
    }
}