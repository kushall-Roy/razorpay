package com.kushal.razorpay.payment.processor.strategy;

import com.kushal.razorpay.common.util.RandomizerUtil;
import com.kushal.razorpay.payment.processor.PaymentProcessor;
import com.kushal.razorpay.payment.processor.dto.PaymentProcessorRequest;
import com.kushal.razorpay.payment.processor.dto.PaymentProcessorResponse;

public class UpiPaymentProcessor implements PaymentProcessor {
    @Override
    public PaymentProcessorResponse charge(PaymentProcessorRequest request) {

        final String VPA_CODE_FAIL = "fail@okaxis";

        String bankCode = request.methodDetails() != null ?
                request.methodDetails().get("vpa").toString() : null;

        //simulation
        if(VPA_CODE_FAIL.equals(bankCode)){
            return new PaymentProcessorResponse.Failure("UPI_REJECTED",
                    "Bank rejected the transaction registration");
        }

        String processorRef = "UPI_PROCESSOR_"+ RandomizerUtil.randomBase64(16);
       // String bankRef = "BANK_REF"+RandomizerUtil.randomBase64(16);
        //No need for this bankRef, because it will come from NPCI switch, not from here

        return new PaymentProcessorResponse.Pending(processorRef);
    }
}
