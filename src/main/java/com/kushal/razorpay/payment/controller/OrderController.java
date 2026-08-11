package com.kushal.razorpay.payment.controller;
import com.kushal.razorpay.payment.dto.request.CreateOrderRequest;
import com.kushal.razorpay.payment.dto.response.OrderResponse;
import com.kushal.razorpay.payment.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;

@RestController
@RequestMapping("/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    UUID merchantId = UUID.fromString("f1be7c4d-7965-49c4-83dc-0e2f199fed0f"); // TODO : replace it with MerchantContext

    @PostMapping
    public ResponseEntity<OrderResponse> create(@RequestBody @Valid CreateOrderRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.create(merchantId,request));
    }
}
