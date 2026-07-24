package com.kushal.razorpay.payment.processor;

import com.kushal.razorpay.common.enums.PaymentMethod;
import com.kushal.razorpay.payment.processor.dto.PaymentProcessorRequest;
import com.kushal.razorpay.payment.processor.dto.PaymentProcessorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PaymentProcessorRouter {

    private final Map<PaymentMethod,PaymentProcessor> paymentProcessor;

    public PaymentProcessorResponse charge(PaymentProcessorRequest request){

        PaymentProcessor processor = paymentProcessor.get(request.method());
        if(processor == null){
            throw new IllegalArgumentException("No Payment Processor registered for method " + request.method());
        }

        return processor.charge(request);

    }
}
