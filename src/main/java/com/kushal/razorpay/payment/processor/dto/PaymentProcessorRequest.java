package com.kushal.razorpay.payment.processor.dto;

import com.kushal.razorpay.common.entity.Money;
import com.kushal.razorpay.common.enums.PaymentMethod;
import java.util.Map;

public record PaymentProcessorRequest(
        PaymentMethod method,
        Money money,
        Map<String,Object> methodDetails
) {
}
