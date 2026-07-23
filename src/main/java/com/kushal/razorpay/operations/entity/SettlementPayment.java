package com.kushal.razorpay.operations.entity;
import com.kushal.razorpay.common.entity.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name= "settlement_payment")
public class SettlementPayment {

    @EmbeddedId
    private SettlementPaymentId id;

    @MapsId("settlementId")
    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "settlement_id",nullable = false)
    private Settlement settlement;

    /* Basically don't want to create new column for the settlement, Just want to have a way to get
    * hold off this settlement whenever inside the SettlementPayment, hence want to map the Settlement column
    * with an id from SettlementPaymentId
    * */



}
