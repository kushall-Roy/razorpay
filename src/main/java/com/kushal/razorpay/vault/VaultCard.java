package com.kushal.razorpay.vault;
import com.kushal.razorpay.common.entity.BaseEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="vault_Card")
public class VaultCard extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false,length = 4)
    private String lastFour;

    @Column(nullable = false,length = 6)
    private String bin;  // First 6 digits of a Card

    @Column(nullable = false)
    private byte[] encryptedPan;

    @Column(nullable = false)
    private byte[] encryptedDek;

    @Column(nullable = false)
    private String brand;  // VISA, RUPAY

    @Column(nullable = false)
    private String expiryMonth;

    @Column(nullable = false)
    private String expiryYear;

    @Column(nullable = false)
    private String cardHolderName;

    private LocalDateTime deletedAt;
}

/*
 dek = awssdk
 PAN = 123456

encrypted_pan = encrypt(PAN,dek)
encrypted_dek = encrypt(dek,master_key)

dek = decrypt(encrypted_dek,master_key)
pan = decrypt(encrypted_pan,dek)
 */
