package com.kushal.razorpay.payment.config;

import com.kushal.razorpay.common.enums.PaymentMethod;
import com.kushal.razorpay.payment.gateway.PaymentAdapter;
import com.kushal.razorpay.payment.gateway.adapter.CardPaymentAdapter;
import com.kushal.razorpay.payment.gateway.adapter.NetBankingAdapter;
import com.kushal.razorpay.payment.gateway.adapter.UpiPaymentAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Map;

@Configuration
public class PaymentAdapterConfig {

    @Bean
    public Map<PaymentMethod, PaymentAdapter> paymentAdapterMap(){
          return Map.of(
                  PaymentMethod.CARD, new CardPaymentAdapter(),
                  PaymentMethod.NETBANKING , new NetBankingAdapter(),
                  PaymentMethod.UPI, new UpiPaymentAdapter()
          );
    }
}
