package com.kushal.razorpay.payment.service.impl;
import com.kushal.razorpay.common.enums.OrderStatus;
import com.kushal.razorpay.common.enums.PaymentStatus;
import com.kushal.razorpay.common.exception.BusinessRuleViolationException;
import com.kushal.razorpay.common.exception.ResourceNotFoundException;
import com.kushal.razorpay.payment.dto.request.PaymentInitRequest;
import com.kushal.razorpay.payment.dto.response.PaymentResponse;
import com.kushal.razorpay.payment.entity.OrderRecord;
import com.kushal.razorpay.payment.entity.Payment;
import com.kushal.razorpay.payment.gateway.PaymentGatewayRouter;
import com.kushal.razorpay.payment.gateway.dto.PaymentRequest;
import com.kushal.razorpay.payment.gateway.dto.PaymentResult;
import com.kushal.razorpay.payment.mapper.PaymentMapper;
import com.kushal.razorpay.payment.repository.OrderRepository;
import com.kushal.razorpay.payment.repository.PaymentRepository;
import com.kushal.razorpay.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentGatewayRouter paymentGatewayRouter;
    private final PaymentMapper paymentMapper;

    @Override
    @Transactional
    public PaymentResponse initiate(UUID merchantId, PaymentInitRequest request) {

        OrderRecord order = orderRepository.findByIdAndMerchantId(request.orderId(), merchantId)
                .orElseThrow(()-> new ResourceNotFoundException("order", request.orderId()));

        if(order.getOrderStatus() != OrderStatus.CREATED && order.getOrderStatus() != OrderStatus.ATTEMPTED){
            throw new BusinessRuleViolationException("ORDER_NOT_PAYABLE",
                    "Order cannot accept payment in status : "+order.getOrderStatus());
        }

        order.setOrderStatus(OrderStatus.ATTEMPTED);
        order.setAttempts(order.getAttempts() + 1);

        Payment payment = Payment.builder()
                .order(order)
                .merchantId(merchantId)
                .amount(order.getAmount())
                .status(PaymentStatus.CREATED)
                .method(request.method())
                .methodDetails(request.methodDetails())
                .build();

        payment = paymentRepository.save(payment);

        PaymentRequest paymentRequest = new PaymentRequest(payment.getId(),
                                   request.orderId(),
                                   merchantId,
                                   order.getAmount(),
                                   request.method(),
                                   request.methodDetails());

        PaymentResult result = paymentGatewayRouter.initiate(paymentRequest);

        switch (result) {
            case PaymentResult.Pending(String registrationRef) -> payment.setProcessorReference(registrationRef);
            case PaymentResult.Failure(String errorCode, String errorDescription) -> {
                payment.setStatus(PaymentStatus.FAILED);
                payment.setErrorCode(errorCode);
                payment.setErrorDescription(errorDescription);
            }
        }
        payment = paymentRepository.save(payment);
        orderRepository.save(order);

        return paymentMapper.toResponse(payment);
    }
}
