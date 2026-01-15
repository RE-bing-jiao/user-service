package com.example.internship.microservice.model.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
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
public class UserCreateRequestDto {

    @NotBlank(message = "Name cannot be blank")
    @Size(max = 255, message = "Name cannot exceed 255 characters")
    private String name;

    @NotBlank(message = "Surname cannot be blank")
    @Size(max = 255, message = "Surname cannot exceed 255 characters")
    private String surname;

    @NotNull(message = "Birth date cannot be null")
    @Past(message = "Birth date must be in the past")
    private LocalDate birthDate;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be valid")
    @Size(max = 255, message = "Email cannot exceed 255 characters")
    private String email;
}