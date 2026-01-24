package com.example.internship.microservice.model.dto.paymentcard;

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
public class PaymentCardUpdateRequestDto {

    @Size(max = 255, message = "Card holder cannot exceed 255 characters")
    private String holder;

    @Future(message = "Expiration date must be in the future")
    private LocalDate expirationDate;

    private Boolean active;
}