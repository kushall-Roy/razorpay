package com.kushal.razorpay.payment.processor.strategy;

import com.kushal.razorpay.payment.processor.PaymentProcessor;
import com.kushal.razorpay.payment.processor.dto.PaymentProcessorRequest;
import com.kushal.razorpay.payment.processor.dto.PaymentProcessorResponse;

public class CardPaymentProcessor implements PaymentProcessor {

    @Override
    public PaymentProcessorResponse charge(PaymentProcessorRequest request) {
        return null;
    }
}
