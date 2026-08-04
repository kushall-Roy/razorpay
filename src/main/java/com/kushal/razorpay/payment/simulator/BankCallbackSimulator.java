package com.kushal.razorpay.payment.simulator;

import com.kushal.razorpay.common.enums.PaymentStatus;
import com.kushal.razorpay.payment.entity.Payment;
import com.kushal.razorpay.payment.repository.PaymentRepository;
import com.kushal.razorpay.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class BankCallbackSimulator {

    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;
    private final SimulatorConfig simulatorConfig;

    //These are the Bank callbacks we are simulating here
    @Scheduled(fixedDelayString = "${payment.simulator.poll-interval-ms:5000}")
    public void processCallBacks(){

        LocalDateTime globalWindow = LocalDateTime.now().minusSeconds(1);

        List<Payment> candidates = paymentRepository.
                findByStatusAndCreatedAtBefore(PaymentStatus.AUTHORIZING,globalWindow);
        //so now we have all the payments that need to process

        if(candidates.isEmpty()) return;

        for(Payment payment : candidates){
            simulateCallback(payment);
        }
    }

    private void simulateCallback(Payment payment) {

    }
}
