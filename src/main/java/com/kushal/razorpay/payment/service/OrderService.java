package com.kushal.razorpay.payment.service;
import com.kushal.razorpay.payment.dto.request.CreateOrderRequest;
import com.kushal.razorpay.payment.dto.response.OrderResponse;
import java.util.UUID;

public interface OrderService {
    OrderResponse create(UUID merchantId, CreateOrderRequest request);
}
