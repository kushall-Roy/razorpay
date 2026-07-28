package com.kushal.razorpay.payment.config;

import com.kushal.razorpay.common.enums.PaymentMethod;
import com.kushal.razorpay.payment.processor.PaymentProcessor;
import com.kushal.razorpay.payment.processor.strategy.CardPaymentProcessor;
import com.kushal.razorpay.payment.processor.strategy.NetBankingPaymentProcessor;
import com.kushal.razorpay.payment.processor.strategy.UpiPaymentProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Map;

@Configuration
public class PaymentProcessorConfig {

    @Bean
    public Map<PaymentMethod, PaymentProcessor> paymentProcessorMap(){
        return Map.of(
                PaymentMethod.CARD, new CardPaymentProcessor(),
                PaymentMethod.NETBANKING, new NetBankingPaymentProcessor(),
                PaymentMethod.UPI, new UpiPaymentProcessor()
        );
    }

}
