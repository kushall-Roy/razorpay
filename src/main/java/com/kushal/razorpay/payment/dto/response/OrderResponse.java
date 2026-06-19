package com.kushal.razorpay.payment.dto.response;
import com.kushal.razorpay.common.entity.Money;
import com.kushal.razorpay.common.enums.OrderStatus;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public record OrderResponse(
        UUID id,  //order id inside razor pay
        UUID merchantId,
        String receipt,
        Money amount,
        OrderStatus status,
        Integer attempts,
        Map<String,Object> notes,
        LocalDateTime expiresAt,
        LocalDateTime createdAt
) {
}
