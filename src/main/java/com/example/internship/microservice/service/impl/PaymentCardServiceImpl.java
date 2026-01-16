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
import com.example.internship.microservice.utils.LogMessages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentCardServiceImpl implements PaymentCardService {

    private final PaymentCardRepository paymentCardRepository;
    private final UserRepository userRepository;
    private final PaymentCardMapper paymentCardMapper;

    @Override
    public PaymentCardDto createCard(PaymentCardCreateRequestDto request) {
        log.info(LogMessages.METHOD_START, "createCard");

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> {
                    log.warn(LogMessages.CARD_USER_NOT_FOUND, request.getUserId());
                    return new ResourceNotFoundException("User not found with id: " + request.getUserId());
                });

        long cardCount = paymentCardRepository.countByUserId(request.getUserId());
        if (cardCount >= 5) {
            log.warn(LogMessages.CARD_LIMIT_EXCEEDED, request.getUserId());
            throw new CardLimitExceededException("User cannot have more than 5 payment cards");
        }

        PaymentCard card = paymentCardMapper.toEntity(request);
        card.setUser(user);
        card.setActive(true);
        PaymentCard savedCard = paymentCardRepository.save(card);
        PaymentCardDto cardDto = paymentCardMapper.toDto(savedCard);

        log.info(LogMessages.CARD_CREATED, cardDto.getId(), request.getUserId());
        log.info(LogMessages.METHOD_END, "createCard");

        return cardDto;
    }

    @Override
    public PaymentCardDto getCardById(Long id) {
        log.info(LogMessages.METHOD_START, "getCardById");

        PaymentCard card = paymentCardRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn(LogMessages.CARD_NOT_FOUND, id);
                    return new ResourceNotFoundException("Payment card not found with id: " + id);
                });

        PaymentCardDto cardDto = paymentCardMapper.toDto(card);
        log.info(LogMessages.CARD_RETRIEVED, cardDto.getId());
        log.info(LogMessages.METHOD_END, "getCardById");

        return cardDto;
    }

    @Override
    public PagedResponse<PaymentCardDto> getAllCards(int page, int size) {
        log.info(LogMessages.METHOD_START, "getAllCards");

        Pageable pageable = PageRequest.of(page, size);
        Page<PaymentCard> cardPage = paymentCardRepository.findAll(pageable);
        PagedResponse<PaymentCardDto> response = PagedResponse.of(cardPage.map(paymentCardMapper::toDto));

        log.info(LogMessages.CARDS_RETRIEVED, cardPage.getContent().size(), page, size);
        log.info(LogMessages.METHOD_END, "getAllCards");

        return response;
    }

    @Override
    public PagedResponse<PaymentCardDto> getCardsByUserId(Long userId, int page, int size) {
        log.info(LogMessages.METHOD_START, "getCardsByUserId");

        Pageable pageable = PageRequest.of(page, size);
        Page<PaymentCard> cardPage = paymentCardRepository.findByUserId(userId, pageable);
        PagedResponse<PaymentCardDto> response = PagedResponse.of(cardPage.map(paymentCardMapper::toDto));

        log.info(LogMessages.CARDS_BY_USER_RETRIEVED, cardPage.getContent().size(), userId, page, size);
        log.info(LogMessages.METHOD_END, "getCardsByUserId");

        return response;
    }

    @Override
    @Transactional
    public PaymentCardDto updateCard(Long id, PaymentCardUpdateRequestDto request) {
        log.info(LogMessages.METHOD_START, "updateCard");

        PaymentCard card = paymentCardRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn(LogMessages.CARD_NOT_FOUND, id);
                    return new ResourceNotFoundException("Payment card not found with id: " + id);
                });

        paymentCardMapper.updateEntityFromDto(request, card);
        PaymentCard updatedCard = paymentCardRepository.save(card);
        PaymentCardDto cardDto = paymentCardMapper.toDto(updatedCard);

        log.info(LogMessages.CARD_UPDATED, cardDto.getId());
        log.info(LogMessages.METHOD_END, "updateCard");

        return cardDto;
    }

    @Override
    @Transactional
    public PaymentCardDto activateCard(Long id) {
        log.info(LogMessages.METHOD_START, "activateCard");

        PaymentCard card = paymentCardRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn(LogMessages.CARD_NOT_FOUND, id);
                    return new ResourceNotFoundException("Payment card not found with id: " + id);
                });

        card.setActive(true);
        PaymentCard activatedCard = paymentCardRepository.save(card);
        PaymentCardDto cardDto = paymentCardMapper.toDto(activatedCard);

        log.info(LogMessages.CARD_ACTIVATED, cardDto.getId());
        log.info(LogMessages.METHOD_END, "activateCard");

        return cardDto;
    }

    @Override
    @Transactional
    public PaymentCardDto deactivateCard(Long id) {
        log.info(LogMessages.METHOD_START, "deactivateCard");

        PaymentCard card = paymentCardRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn(LogMessages.CARD_NOT_FOUND, id);
                    return new ResourceNotFoundException("Payment card not found with id: " + id);
                });

        card.setActive(false);
        PaymentCard deactivatedCard = paymentCardRepository.save(card);
        PaymentCardDto cardDto = paymentCardMapper.toDto(deactivatedCard);

        log.info(LogMessages.CARD_DEACTIVATED, cardDto.getId());
        log.info(LogMessages.METHOD_END, "deactivateCard");

        return cardDto;
    }

    @Override
    @Transactional
    public void deleteCard(Long id) {
        log.info(LogMessages.METHOD_START, "deleteCard");

        PaymentCard card = paymentCardRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn(LogMessages.CARD_NOT_FOUND, id);
                    return new ResourceNotFoundException("Payment card not found with id: " + id);
                });

        paymentCardRepository.delete(card);

        log.info(LogMessages.CARD_DELETED, id);
        log.info(LogMessages.METHOD_END, "deleteCard");
    }
}