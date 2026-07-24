package com.kushal.razorpay.operations.entity;
import com.kushal.razorpay.common.entity.BaseEntity;
import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public class SettlementPaymentId {

    private UUID settlementId;
    private UUID paymentId;
}
