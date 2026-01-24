package com.example.internship.microservice.service;

import com.example.internship.microservice.model.dto.PagedResponse;
import com.example.internship.microservice.model.dto.paymentcard.PaymentCardCreateRequestDto;
import com.example.internship.microservice.model.dto.paymentcard.PaymentCardDto;
import com.example.internship.microservice.model.dto.paymentcard.PaymentCardUpdateRequestDto;

public interface PaymentCardService {
    /**
     * Creates a new payment card.
     * @param request DTO containing card creation details
     * @return created card as DTO
     */
    PaymentCardDto createCard(PaymentCardCreateRequestDto request);

    /**
     * Retrieves a payment card by its ID.
     * @param id card identifier
     * @return card details as DTO
     */
    PaymentCardDto getCardById(Long id);

    /**
     * Gets all payment cards with pagination.
     * @param page page number
     * @param size number of items per page
     * @return paginated list of cards
     */
    PagedResponse<PaymentCardDto> getAllCards(int page, int size);

    /**
     * Gets all payment cards for a specific user with pagination.
     * @param userId user identifier
     * @param page page number
     * @param size number of items per page
     * @return paginated list of user’s cards
     */
    PagedResponse<PaymentCardDto> getCardsByUserId(Long userId, int page, int size);

    /**
     * Updates an existing payment card.
     * @param id card identifier to update
     * @param request DTO with updated card data
     * @return updated card as DTO
     */
    PaymentCardDto updateCard(Long id, PaymentCardUpdateRequestDto request);

    /**
     * Activates a payment card.
     * @param id card identifier
     * @return activated card as DTO
     */
    PaymentCardDto activateCard(Long id);

    /**
     * Deactivates a payment card.
     * @param id card identifier
     * @return deactivated card as DTO
     */
    PaymentCardDto deactivateCard(Long id);

    /**
     * Deletes a payment card permanently.
     * @param id card identifier
     */
    void deleteCard(Long id);
}