package com.kushal.razorpay.payment.statemachine;

import com.kushal.razorpay.common.enums.PaymentActor;
import com.kushal.razorpay.common.enums.PaymentEvent;
import com.kushal.razorpay.common.enums.PaymentStatus;
import com.kushal.razorpay.payment.entity.Payment;
import com.kushal.razorpay.payment.entity.PaymentTransitionLog;
import com.kushal.razorpay.payment.repository.PaymentTransitionLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentTransitionService {

    private final PaymentTransitionLogRepository paymentTransitionLogRepository;
    private final PaymentStateMachine paymentStateMachine;

    public PaymentStatus apply(Payment payment, PaymentEvent paymentEvent){

        PaymentStatus next = paymentStateMachine.transition(payment.getStatus(), paymentEvent);

        PaymentTransitionLog log = PaymentTransitionLog.builder()
                .payment(payment)
                .fromStatus(payment.getStatus())
                .event(paymentEvent)
                .toStatus(next)
                .actor(PaymentActor.SYSTEM)  //TODO: fetch merchant context to identify actor
                .occuredAt(LocalDateTime.now())
                .build();

        payment.setStatus(next);
        paymentTransitionLogRepository.save(log);
        return next;
    }

}
