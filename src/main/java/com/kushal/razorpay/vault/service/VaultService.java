package com.kushal.razorpay.vault.service;

import com.kushal.razorpay.common.entity.Money;
import com.kushal.razorpay.payment.processor.dto.PaymentProcessorResponse;
import com.kushal.razorpay.vault.dto.request.TokenizeRequest;
import com.kushal.razorpay.vault.dto.response.TokenizeResponse;
import java.util.Map;
import java.util.UUID;

public interface VaultService{
    TokenizeResponse tokenize(TokenizeRequest request, UUID merchantId);
    PaymentProcessorResponse charge(UUID paymentId,String token, Money amount, Map<String, Object> methodDetails);
}
