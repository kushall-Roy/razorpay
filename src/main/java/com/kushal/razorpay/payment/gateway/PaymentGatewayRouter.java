package com.kushal.razorpay.payment.gateway;
import com.kushal.razorpay.common.enums.PaymentMethod;
import com.kushal.razorpay.payment.gateway.dto.PaymentRequest;
import com.kushal.razorpay.payment.gateway.dto.PaymentResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PaymentGatewayRouter {

    private final Map<PaymentMethod, PaymentAdapter> paymentAdapters;

    public PaymentResult initiate(PaymentRequest paymentRequest){

        PaymentAdapter adapter = paymentAdapters.get(paymentRequest.method());
        if(adapter == null){
            throw new IllegalArgumentException("No payment adapter registered for method :: "+paymentRequest.method());
        }
        return adapter.initiate(paymentRequest);
    }
}
