package com.example.internship.microservice.controller;

import com.example.internship.microservice.model.dto.ApiResponse;
import com.example.internship.microservice.model.dto.PagedResponse;
import com.example.internship.microservice.model.dto.paymentcard.PaymentCardCreateRequestDto;
import com.example.internship.microservice.model.dto.paymentcard.PaymentCardDto;
import com.example.internship.microservice.model.dto.paymentcard.PaymentCardUpdateRequestDto;
import com.example.internship.microservice.service.PaymentCardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/payment-cards")
@RequiredArgsConstructor
public class PaymentCardController {

    private final PaymentCardService paymentCardService;

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentCardDto>> createCard(@Valid @RequestBody PaymentCardCreateRequestDto request) {
        PaymentCardDto card = paymentCardService.createCard(request);
        return ResponseEntity.ok(ApiResponse.success(card, "Payment card created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentCardDto>> getCardById(@PathVariable Long id) {
        PaymentCardDto card = paymentCardService.getCardById(id);
        return ResponseEntity.ok(ApiResponse.success(card, "Payment card retrieved successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<PaymentCardDto>>> getAllCards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PagedResponse<PaymentCardDto> cards = paymentCardService.getAllCards(page, size);
        return ResponseEntity.ok(ApiResponse.success(cards, "Payment cards retrieved successfully"));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<PagedResponse<PaymentCardDto>>> getCardsByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PagedResponse<PaymentCardDto> cards = paymentCardService.getCardsByUserId(userId, page, size);
        return ResponseEntity.ok(ApiResponse.success(cards, "Payment cards retrieved successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentCardDto>> updateCard(
            @PathVariable Long id,
            @Valid @RequestBody PaymentCardUpdateRequestDto request) {
        PaymentCardDto card = paymentCardService.updateCard(id, request);
        return ResponseEntity.ok(ApiResponse.success(card, "Payment card updated successfully"));
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<PaymentCardDto>> activateCard(@PathVariable Long id) {
        PaymentCardDto card = paymentCardService.activateCard(id);
        return ResponseEntity.ok(ApiResponse.success(card, "Payment card activated successfully"));
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<PaymentCardDto>> deactivateCard(@PathVariable Long id) {
        PaymentCardDto card = paymentCardService.deactivateCard(id);
        return ResponseEntity.ok(ApiResponse.success(card, "Payment card deactivated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCard(@PathVariable Long id) {
        paymentCardService.deleteCard(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Payment card deleted successfully"));
    }
}