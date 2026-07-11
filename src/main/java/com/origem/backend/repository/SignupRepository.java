package com.origem.backend.repository;

import com.origem.backend.domain.Signup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SignupRepository extends JpaRepository<Signup, UUID> {
    Optional<Signup> findByStripePaymentIntentId(String stripePaymentIntentId);
}
