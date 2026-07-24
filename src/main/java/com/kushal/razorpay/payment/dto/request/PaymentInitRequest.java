package com.kushal.razorpay.payment.dto.request;
import com.kushal.razorpay.common.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import java.util.UUID;

public record PaymentInitRequest(

        @NotNull(message = "order id is required")
        UUID orderId,

        @NotNull(message = "Payment method is required")
        PaymentMethod method,

        Map<String,Object> methodDetails
) {
}
