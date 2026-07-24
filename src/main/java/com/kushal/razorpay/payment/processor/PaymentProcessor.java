package com.kushal.razorpay.payment.processor;

import com.kushal.razorpay.payment.processor.dto.PaymentProcessorRequest;
import com.kushal.razorpay.payment.processor.dto.PaymentProcessorResponse;

public interface PaymentProcessor {

    PaymentProcessorResponse charge(PaymentProcessorRequest request);
}
