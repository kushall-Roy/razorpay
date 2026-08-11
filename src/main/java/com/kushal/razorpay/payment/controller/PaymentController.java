package com.kushal.razorpay.payment.controller;

import com.kushal.razorpay.payment.dto.request.PaymentInitRequest;
import com.kushal.razorpay.payment.dto.response.PaymentResponse;
import com.kushal.razorpay.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/v1/payments")
@RestController
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    UUID merchantId = UUID.fromString("f1be7c4d-7965-49c4-83dc-0e2f199fed0f"); // TODO : replace it with MerchantContext

    @PostMapping
    public ResponseEntity<PaymentResponse> initiate(@Valid @RequestBody PaymentInitRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.initiate(merchantId,request));
    }

    @PostMapping("/{paymentId}/capture")
    public ResponseEntity<PaymentResponse> capture(@PathVariable UUID paymentId){
        return ResponseEntity.ok(paymentService.capture(merchantId,paymentId));
    }
}
