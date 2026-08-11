package com.kushal.razorpay.payment.simulator;

import com.kushal.razorpay.common.enums.ChaosMode;
import com.kushal.razorpay.common.enums.PaymentStatus;
import com.kushal.razorpay.common.util.RandomizerUtil;
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
    //@Scheduled(fixedDelayString = "${payment.simulator.poll-interval-ms:5000}")
    public void processCallBacks(){

        LocalDateTime globalWindow = LocalDateTime.now().minusSeconds(1);

        List<Payment> candidates = paymentRepository.
                findByStatusAndCreatedAtBefore(PaymentStatus.AUTHORIZING,globalWindow);
        //so now we have all the payments that need to process

        log.info("Simulating payments for {} payments",candidates.size());

        if(candidates.isEmpty()) return;

        for(Payment payment : candidates){
            simulateCallback(payment);
        }
    }
    private void simulateCallback(Payment payment) {
        SimulatorConfig.MethodSimulatorConfig methodConfig = simulatorConfig.configFor(payment.getMethod());

        LocalDateTime dueAt = dueAt(payment,methodConfig); // It will give us the time when the payment should be processed,before this time payment shouldn't be processed
        //In this payment should go from Authorizing state to Authorized state

        if(LocalDateTime.now().isBefore(dueAt)){
            return; //do nothing just return, we are not going to simulate the payment right now, it can be simulated some time later
        }

        ChaosMode chaosMode = simulatorConfig.getChaosMode();
        switch (chaosMode){
            case  SUCCESS -> resolved(payment,true);
            case FAILURE -> resolved(payment,false);
            case TIMEOUT -> {
                log.debug("BankCallback Simulator : Payment timed out");
            }
            case NORMAL,SLOW -> resolved(payment,shouldApprove(payment,methodConfig));
        }
    }

    private void resolved(Payment payment, boolean approved){
        if(approved){
            String bankRef = "SIM_BANK_REF"+ RandomizerUtil.randomBase64(8);
            paymentService.resolveAuthorization(payment.getId(),true,bankRef,null,null);
        }else{
            paymentService.resolveAuthorization(payment.getId(),false,null,"SIM_BANK_ERROR_CODE","Simulated bank declined");
        }

    }
    private boolean shouldApprove(Payment payment, SimulatorConfig.MethodSimulatorConfig methodConfig){
        int bucket = Math.abs(payment.getId().hashCode()) % 100;
        return bucket < methodConfig.getSuccessRate();
    }
    private LocalDateTime dueAt(Payment payment, SimulatorConfig.MethodSimulatorConfig methodConfig){
        int range = methodConfig.getMaxDelaySeconds() - methodConfig.getMinDelaySeconds();
        int delaySeconds = methodConfig.getMinDelaySeconds() + Math.abs(payment.getId().hashCode()) % (range+1);

        if(simulatorConfig.getChaosMode() == ChaosMode.SLOW){
            delaySeconds *= 2;
        }
        return payment.getCreatedAt().plusSeconds(delaySeconds);
    }
}
