package com.kushal.razorpay.payment.service.impl;
import com.kushal.razorpay.common.enums.OrderStatus;
import com.kushal.razorpay.common.enums.PaymentEvent;
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
import com.kushal.razorpay.payment.repository.PaymentTransitionLogRepository;
import com.kushal.razorpay.payment.service.PaymentService;
import com.kushal.razorpay.payment.statemachine.PaymentTransitionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentGatewayRouter paymentGatewayRouter;
    private final PaymentMapper paymentMapper;
    private final PaymentTransitionService paymentTransitionService;
    private final PaymentTransitionLogRepository paymentTransitionLogRepository;

    @Override
    @Transactional()
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
                .idempotencyKey(UUID.randomUUID().toString()) //TODO: idempotency
                .methodDetails(request.methodDetails())
                .build();

        payment = paymentRepository.save(payment);

        PaymentRequest paymentRequest = new PaymentRequest(payment.getId(),
                                   request.orderId(),
                                   merchantId,
                                   order.getAmount(),
                                   request.method(),
                                   request.methodDetails());

        paymentTransitionService.apply(payment,PaymentEvent.AUTHORIZE_ATTEMPT);
        PaymentResult result = paymentGatewayRouter.initiate(paymentRequest);

        switch (result) {
            case PaymentResult.Pending(String registrationRef) -> payment.setProcessorReference(registrationRef);
            case PaymentResult.Failure(String errorCode, String errorDescription) -> {
                //payment.setStatus(PaymentStatus.FAILED);
                paymentTransitionService.apply(payment, PaymentEvent.AUTHORIZE_FAIL);
                payment.setErrorCode(errorCode);
                payment.setErrorDescription(errorDescription);
            }
            case PaymentResult.Success(String bankReference) -> {
                log.warn("Invalid state");
                return null;
            }
        }
        payment = paymentRepository.save(payment);
        orderRepository.save(order);

        //TODO : Send  an outbox (kafka event)

        return paymentMapper.toResponse(payment);
    }

    @Override
    public PaymentResponse capture(UUID merchantId, UUID paymentId) {

        //first check if the payment is real or not
        Payment payment = paymentRepository.findByIdAndMerchantId(paymentId,merchantId)
                .orElseThrow(()-> new ResourceNotFoundException("payment",paymentId));

        //payment.setStatus(PaymentStatus.CAPTURING); //TODO: StateMachine need here
        paymentTransitionService.apply(payment,PaymentEvent.CAPTURE_REQUEST);

        PaymentResult paymentResult = paymentGatewayRouter.capture(payment.getMethod(),paymentId);

        if(paymentResult instanceof PaymentResult.Success success){
           // payment.setStatus(PaymentStatus.CAPTURED);
            paymentTransitionService.apply(payment,PaymentEvent.CAPTURE_SUCCESS);
            payment.setCapturedAt(LocalDateTime.now());
            log.info("Payment captured, paymentId : {} ",paymentId);
        }else if(paymentResult instanceof PaymentResult.Failure(String errorCode, String errorDescription)){
            //payment.setStatus(PaymentStatus.AUTHORIZED);  //back to authorized again
            paymentTransitionService.apply(payment,PaymentEvent.CAPTURE_FAIL);
            payment.setErrorCode(errorCode);
            payment.setErrorDescription(errorDescription);
            log.warn("Payment captured failed, paymentId : {} ",paymentId);
        }

        payment = paymentRepository.save(payment);
        //TODO : Send  an outbox (kafka event)
        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional
    public void resolveAuthorization(UUID paymentId, boolean approve, String bankRef, String errorCode, String errorDescription) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(()-> new ResourceNotFoundException("Payment",paymentId));

        if(payment.getStatus() != PaymentStatus.AUTHORIZING){
            log.warn("Payment is not in Authorizing state, paymentId : {}, status : {}",paymentId,payment.getStatus());
            return;
        }

        OrderRecord orderRecord = payment.getOrder();

        if(approve){
            paymentTransitionService.apply(payment,PaymentEvent.AUTHORIZE_SUCCESS);
            payment.setBankReference(bankRef);
            payment.setAuthorizedAt(LocalDateTime.now());

            //auto-capture
            paymentTransitionService.apply(payment,PaymentEvent.CAPTURE_REQUEST);
            PaymentResult captureResult = paymentGatewayRouter.capture(payment.getMethod(),paymentId);

            if(captureResult instanceof PaymentResult.Success success){
                paymentTransitionService.apply(payment,PaymentEvent.CAPTURE_SUCCESS);
                payment.setCapturedAt(LocalDateTime.now());
                orderRecord.setOrderStatus(OrderStatus.PAID);
            } else if (captureResult instanceof PaymentResult.Failure(String code, String description)) {
                paymentTransitionService.apply(payment,PaymentEvent.CAPTURE_FAIL);
                payment.setErrorCode(code);
                payment.setErrorDescription(description);
            }
        }else{
            paymentTransitionService.apply(payment,PaymentEvent.AUTHORIZE_FAIL);
            payment.setErrorCode(errorCode);
            payment.setErrorDescription(errorDescription);
        }

        paymentRepository.save(payment);
        orderRepository.save(orderRecord);
    }
}
