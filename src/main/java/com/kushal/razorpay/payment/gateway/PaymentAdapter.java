package com.kushal.razorpay.payment.gateway;
import com.kushal.razorpay.payment.gateway.dto.PaymentRequest;
import com.kushal.razorpay.payment.gateway.dto.PaymentResult;

public interface PaymentAdapter {

    PaymentResult initiate(PaymentRequest request);
}
