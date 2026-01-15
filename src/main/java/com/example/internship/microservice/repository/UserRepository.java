package com.example.internship.microservice.repository;

import com.example.internship.microservice.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    @Query(value = "SELECT * FROM users WHERE active = :active", nativeQuery = true)
    Page<User> findActiveUsersPaginated(@Param("active") Boolean active, Pageable pageable);
}