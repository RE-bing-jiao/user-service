package com.example.internship.microservice.model.dto.paymentcard;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentCardCreateRequestDto {

    @NotNull(message = "User ID cannot be null")
    private Long userId;

    @NotBlank(message = "Card number cannot be blank")
    @Size(max = 255, message = "Card number cannot exceed 255 characters")
    private String number;

    @NotBlank(message = "Card holder cannot be blank")
    @Size(max = 255, message = "Card holder cannot exceed 255 characters")
    private String holder;

    @NotNull(message = "Expiration date cannot be null")
    @Future(message = "Expiration date must be in the future")
    private LocalDate expirationDate;
}