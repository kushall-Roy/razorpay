package com.kushal.razorpay.vault.repository;
import com.kushal.razorpay.vault.entity.CardToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.Optional;

public interface CardTokenRepository extends JpaRepository<CardToken, UUID> {
    Optional<CardToken> findByTokenAndRevokedAtIsNull(String token);
}
