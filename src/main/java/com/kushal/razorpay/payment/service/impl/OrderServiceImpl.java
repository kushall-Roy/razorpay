package com.kushal.razorpay.payment.service.impl;
import com.kushal.razorpay.common.enums.OrderStatus;
import com.kushal.razorpay.common.exception.DuplicateResourceException;
import com.kushal.razorpay.payment.dto.request.CreateOrderRequest;
import com.kushal.razorpay.payment.dto.response.OrderResponse;
import com.kushal.razorpay.payment.entity.OrderRecord;
import com.kushal.razorpay.payment.repository.OrderRepository;
import com.kushal.razorpay.payment.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Value("${payment.order.default-order-expiry-minutes:30}")
    private int defaultOrderExpiryMinutes;

    @Override
    public OrderResponse create(UUID merchantId, CreateOrderRequest request) {

        if(request.receipt() != null && orderRepository.existsByMerchantIdAndReceipt(merchantId,request.receipt())){
            throw new DuplicateResourceException("ORDER_RECEIPT_DUPLICATE","Order with receipt already exists");
        }

        OrderRecord orderRecord = OrderRecord.builder()
                        .receipt(request.receipt())
                        .amount(request.amount())
                         .notes(request.notes())
                         .merchantId(merchantId)
                        .orderStatus(OrderStatus.CREATED)
                        .expiresAt(request.expiresAt() != null ? request.expiresAt() :
                        LocalDateTime.now().plusMinutes(defaultOrderExpiryMinutes)).build();

        orderRecord = orderRepository.save(orderRecord);

        // TODO: publish Kafka event about order creation

        return new OrderResponse(orderRecord.getId(),
                orderRecord.getMerchantId(),
                orderRecord.getReceipt(),
                orderRecord.getAmount(),
                orderRecord.getOrderStatus(),
                orderRecord.getAttempts(),
                orderRecord.getNotes(),orderRecord.getExpiresAt(),null);
    }
}
