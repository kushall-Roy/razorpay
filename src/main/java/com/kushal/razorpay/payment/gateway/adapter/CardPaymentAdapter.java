package com.kushal.razorpay.payment.gateway.adapter;
import com.kushal.razorpay.payment.gateway.PaymentAdapter;
import com.kushal.razorpay.payment.gateway.dto.PaymentRequest;
import com.kushal.razorpay.payment.gateway.dto.PaymentResult;
import com.kushal.razorpay.payment.processor.dto.PaymentProcessorResponse;
import com.kushal.razorpay.vault.service.VaultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class CardPaymentAdapter implements PaymentAdapter {

    private final VaultService vaultService;

    @Override
        public PaymentResult initiate(PaymentRequest request) {

        String token = (String) request.methodDetails().get("token");

        PaymentProcessorResponse response =vaultService.charge(request.paymentId(),token,request.amount(),request.methodDetails());

        return switch (response)  {
            case PaymentProcessorResponse.Success success -> new PaymentResult.Success(success.bankReference());
            case PaymentProcessorResponse.Failure failure -> new PaymentResult.Failure(failure.errorCode(), failure.errorDescription());
            case PaymentProcessorResponse.Pending pending -> new PaymentResult.Pending(pending.processorReference());

        };
    }

    @Override
    public PaymentResult capture(UUID paymentId) {
        return new PaymentResult.Success("CARD_REF");
    }
}
