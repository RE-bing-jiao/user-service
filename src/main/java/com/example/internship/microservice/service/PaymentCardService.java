package com.example.internship.microservice.service;

import com.example.internship.microservice.model.dto.PagedResponse;
import com.example.internship.microservice.model.dto.paymentcard.PaymentCardCreateRequestDto;
import com.example.internship.microservice.model.dto.paymentcard.PaymentCardDto;
import com.example.internship.microservice.model.dto.paymentcard.PaymentCardUpdateRequestDto;

public interface PaymentCardService {

    PaymentCardDto createCard(PaymentCardCreateRequestDto request);

    PaymentCardDto getCardById(Long id);

    PagedResponse<PaymentCardDto> getAllCards(int page, int size);

    PagedResponse<PaymentCardDto> getCardsByUserId(Long userId, int page, int size);

    PaymentCardDto updateCard(Long id, PaymentCardUpdateRequestDto request);

    PaymentCardDto activateCard(Long id);

    PaymentCardDto deactivateCard(Long id);

    void deleteCard(Long id);
}