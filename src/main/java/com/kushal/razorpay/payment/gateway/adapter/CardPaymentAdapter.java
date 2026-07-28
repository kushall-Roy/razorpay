package com.kushal.razorpay.payment.gateway.adapter;
import com.kushal.razorpay.payment.gateway.PaymentAdapter;
import com.kushal.razorpay.payment.gateway.dto.PaymentRequest;
import com.kushal.razorpay.payment.gateway.dto.PaymentResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class CardPaymentAdapter implements PaymentAdapter {

    @Override
    public PaymentResult initiate(PaymentRequest request) {

        return null;

    }

    @Override
    public PaymentResult capture(UUID paymentId) {
        return null;
    }
}
