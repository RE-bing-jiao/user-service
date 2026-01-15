package com.example.internship.microservice.repository;

import com.example.internship.microservice.model.entity.PaymentCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PaymentCardRepository extends JpaRepository<PaymentCard, Long> {

    List<PaymentCard> findByUserId(Long userId);

    Page<PaymentCard> findByUserId(Long userId, Pageable pageable);

    List<PaymentCard> findByUserIdAndActiveTrue(Long userId);

    @Query("SELECT COUNT(pc) FROM PaymentCard pc WHERE pc.user.id = :userId")
    Long countByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE PaymentCard pc SET pc.active = false WHERE pc.user.id = :userId")
    void deactivateAllCardsByUserId(@Param("userId") Long userId);
}


