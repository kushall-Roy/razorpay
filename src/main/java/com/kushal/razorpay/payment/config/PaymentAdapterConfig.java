package com.kushal.razorpay.payment.config;

import com.kushal.razorpay.common.enums.PaymentMethod;
import com.kushal.razorpay.payment.gateway.PaymentAdapter;
import com.kushal.razorpay.payment.gateway.adapter.CardPaymentAdapter;
import com.kushal.razorpay.payment.gateway.adapter.NetBankingAdapter;
import com.kushal.razorpay.payment.gateway.adapter.UpiPaymentAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class PaymentAdapterConfig {

    private final CardPaymentAdapter cardPaymentAdapter;
    private final UpiPaymentAdapter upiPaymentAdapter;
    private final NetBankingAdapter netBankingAdapter;

    @Bean
    public Map<PaymentMethod, PaymentAdapter> paymentAdapterMap(){
          return Map.of(
                  PaymentMethod.CARD, cardPaymentAdapter,
                  PaymentMethod.NETBANKING , netBankingAdapter,
                  PaymentMethod.UPI, upiPaymentAdapter
          );
    }
}
