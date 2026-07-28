package com.kushal.razorpay.payment.statemachine;
import com.kushal.razorpay.common.enums.PaymentEvent;
import com.kushal.razorpay.common.enums.PaymentStatus;
import com.kushal.razorpay.common.exception.InvalidStateTransitionException;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class PaymentStateMachine {

    private record Transition(PaymentStatus from, PaymentEvent event){}

    private static final Map<Transition,PaymentStatus> TRANSITION = Map.ofEntries(
            Map.entry(new Transition(PaymentStatus.CREATED,PaymentEvent.AUTHORIZE_ATTEMPT),PaymentStatus.AUTHORIZING),
            Map.entry(new Transition(PaymentStatus.AUTHORIZING,PaymentEvent.AUTHORIZE_SUCCESS),PaymentStatus.AUTHORIZED),
            Map.entry(new Transition(PaymentStatus.AUTHORIZING,PaymentEvent.AUTHORIZE_FAIL),PaymentStatus.FAILED),
            Map.entry(new Transition(PaymentStatus.AUTHORIZED,PaymentEvent.CAPTURE_REQUEST),PaymentStatus.CAPTURING),
            Map.entry(new Transition(PaymentStatus.CAPTURING,PaymentEvent.CAPTURE_SUCCESS),PaymentStatus.CAPTURED),
            Map.entry(new Transition(PaymentStatus.CAPTURING,PaymentEvent.CAPTURE_FAIL),PaymentStatus.AUTHORIZED),
            Map.entry(new Transition(PaymentStatus.CAPTURED,PaymentEvent.REFUND_INIT),PaymentStatus.PARTIALLY_REFUNDED),
            Map.entry(new Transition(PaymentStatus.PARTIALLY_REFUNDED,PaymentEvent.REFUND_COMPLETE),PaymentStatus.REFUNDED),
            Map.entry(new Transition(PaymentStatus.CAPTURED,PaymentEvent.REFUND_COMPLETE),PaymentStatus.REFUNDED),
            Map.entry(new Transition(PaymentStatus.CAPTURED,PaymentEvent.SETTLE),PaymentStatus.SETTLED),
            Map.entry(new Transition(PaymentStatus.SETTLED,PaymentEvent.REFUND_INIT),PaymentStatus.PARTIALLY_REFUNDED),
            Map.entry(new Transition(PaymentStatus.CREATED,PaymentEvent.CANCEL),PaymentStatus.CANCELLED),
            Map.entry(new Transition(PaymentStatus.AUTHORIZING,PaymentEvent.CANCEL),PaymentStatus.CANCELLED),
            Map.entry(new Transition(PaymentStatus.AUTHORIZED,PaymentEvent.CAPTURE_TIMEOUT),PaymentStatus.AUTH_EXPIRED)
    );

    public PaymentStatus transition(PaymentStatus current,PaymentEvent event){
        PaymentStatus next = TRANSITION.get(new Transition(current,event));
        if(next == null){
            throw new InvalidStateTransitionException(current.name(),event.name());
        }
        return next;
    }
}

/*
PARTIALLY_REFUNDED means we have started or initiated refund process, and once the refund has been submitted
then we will get the call back again, in that case we would be inside PARTIALLY_REFUNDED state, and will getGet
REFUND_COMPLETE notification, in which case we will move this status to REFUNDED state
*/
