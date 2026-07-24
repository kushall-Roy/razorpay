package com.kushal.razorpay.payment.processor.strategy;

import com.kushal.razorpay.payment.processor.PaymentProcessor;
import com.kushal.razorpay.payment.processor.dto.PaymentProcessorRequest;
import com.kushal.razorpay.payment.processor.dto.PaymentProcessorResponse;

public class NetBankingPaymentProcessor implements PaymentProcessor {

    @Override
    public PaymentProcessorResponse charge(PaymentProcessorRequest request) {

        //call third party
        return null;
    }
}
