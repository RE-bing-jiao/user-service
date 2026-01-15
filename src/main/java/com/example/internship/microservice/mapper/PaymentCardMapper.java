package com.example.internship.microservice.mapper;

import com.example.internship.microservice.model.dto.paymentcard.PaymentCardCreateRequestDto;
import com.example.internship.microservice.model.dto.paymentcard.PaymentCardDto;
import com.example.internship.microservice.model.dto.paymentcard.PaymentCardUpdateRequestDto;
import com.example.internship.microservice.model.entity.PaymentCard;
import org.mapstruct.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PaymentCardMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    PaymentCard toEntity(PaymentCardCreateRequestDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "user", ignore = true)
    void updateEntityFromDto(PaymentCardUpdateRequestDto dto, @MappingTarget PaymentCard entity);

    @Mapping(source = "user.id", target = "userId")
    PaymentCardDto toDto(PaymentCard entity);

    List<PaymentCardDto> toDtoList(List<PaymentCard> entities);
}