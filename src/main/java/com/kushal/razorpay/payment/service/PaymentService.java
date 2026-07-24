package com.kushal.razorpay.payment.service;
import com.kushal.razorpay.payment.dto.request.PaymentInitRequest;
import com.kushal.razorpay.payment.dto.response.PaymentResponse;
import java.util.UUID;

public interface PaymentService {

    PaymentResponse initiate(UUID merchantId, PaymentInitRequest request);
}
