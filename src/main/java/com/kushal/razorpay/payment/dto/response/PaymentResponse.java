package com.kushal.razorpay.payment.dto.response;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kushal.razorpay.common.entity.Money;
import com.kushal.razorpay.common.enums.PaymentMethod;
import com.kushal.razorpay.common.enums.PaymentStatus;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL) //it will remove all the fields that are null, and returns only the fiels that are non null to user
public record PaymentResponse(
        UUID id,
        UUID orderId,
        UUID merchantId,
        Money amount,
        PaymentStatus status,
        PaymentMethod method,
        Map<String,Object> methodDetails,
        String cardLastFour,
        String cardBrand,
        String bankReference,
        String errorCode,
        String errorDescription,
        Long refundedAmountPaise,
        LocalDateTime capturedAt,
        LocalDateTime createdAt
) {
}
